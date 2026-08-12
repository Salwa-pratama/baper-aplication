package chat

import (
	"errors"
	"fmt"

	"baper/internal/models"
)

// Batas kewajaran untuk order yang dibuat otomatis oleh AI.
// Pembeli bisa mencoba prompt-injection lewat chat WhatsApp, jadi angka
// yang keluar dari AI tidak boleh dipercaya apa adanya.
const (
	MaxQtyPerItem    = 100
	MaxItemsPerOrder = 20
)

var (
	ErrOrderKosong        = errors.New("order tanpa item")
	ErrTerlaluBanyakItem  = errors.New("jumlah item melewati batas")
	ErrProdukTidakDikenal = errors.New("product_id bukan milik bisnis ini")
	ErrQtyTidakWajar      = errors.New("qty di luar batas wajar")
)

// AIOrderItem adalah bentuk item order yang dikeluarkan AI.
type AIOrderItem struct {
	ProductID string `json:"product_id"`
	Qty       int    `json:"qty"`
}

// AIOrderPayload adalah blok JSON yang diminta dari AI saat pesanan final.
type AIOrderPayload struct {
	IsOrderFinal bool          `json:"is_order_final"`
	Items        []AIOrderItem `json:"items"`
}

// BuildOrderItems memvalidasi output AI dan mengubahnya menjadi OrderItem.
//
// Aturannya:
//   - product_id WAJIB ada di katalog bisnis tersebut (tidak boleh produk asing)
//   - qty harus > 0 dan <= MaxQtyPerItem
//   - item dengan product_id sama digabung, lalu totalnya dicek ulang supaya
//     AI tidak bisa melewati batas dengan mengulang-ulang item yang sama
//   - jumlah item unik dibatasi MaxItemsPerOrder
//
// Price sengaja TIDAK diisi di sini: harga diambil dari database di dalam
// transaksi SaveOrder, supaya harga tidak bisa dipengaruhi output AI.
func BuildOrderItems(products []models.Product, items []AIOrderItem) ([]models.OrderItem, error) {
	if len(items) == 0 {
		return nil, ErrOrderKosong
	}
	if len(items) > MaxItemsPerOrder {
		return nil, fmt.Errorf("%w: %d item", ErrTerlaluBanyakItem, len(items))
	}

	valid := make(map[string]bool, len(products))
	for _, p := range products {
		valid[p.ID] = true
	}

	// Gabungkan qty per product_id, sekaligus jaga urutan kemunculannya
	// supaya hasilnya deterministik (penting untuk test dan untuk log).
	qtyByProduct := make(map[string]int, len(items))
	order := make([]string, 0, len(items))

	for _, item := range items {
		if !valid[item.ProductID] {
			return nil, fmt.Errorf("%w: %q", ErrProdukTidakDikenal, item.ProductID)
		}
		if item.Qty <= 0 || item.Qty > MaxQtyPerItem {
			return nil, fmt.Errorf("%w: qty %d untuk produk %s", ErrQtyTidakWajar, item.Qty, item.ProductID)
		}
		if _, seen := qtyByProduct[item.ProductID]; !seen {
			order = append(order, item.ProductID)
		}
		qtyByProduct[item.ProductID] += item.Qty
	}

	if len(order) > MaxItemsPerOrder {
		return nil, fmt.Errorf("%w: %d produk unik", ErrTerlaluBanyakItem, len(order))
	}

	result := make([]models.OrderItem, 0, len(order))
	for _, productID := range order {
		qty := qtyByProduct[productID]
		// Cek ulang setelah digabung: 60 + 60 untuk produk sama = 120, tolak.
		if qty > MaxQtyPerItem {
			return nil, fmt.Errorf("%w: total qty %d untuk produk %s", ErrQtyTidakWajar, qty, productID)
		}
		result = append(result, models.OrderItem{
			ProductID: productID,
			Quantity:  qty,
		})
	}

	return result, nil
}
