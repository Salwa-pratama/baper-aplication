package chat

import (
	"errors"
	"testing"

	"baper/internal/models"
)

func katalog() []models.Product {
	return []models.Product{
		{ID: "p1", BusinessID: "b1", Name: "Kopi", Price: 25000, Stock: 10},
		{ID: "p2", BusinessID: "b1", Name: "Teh", Price: 15000, Stock: 3},
	}
}

func TestBuildOrderItems_Valid(t *testing.T) {
	items, err := BuildOrderItems(katalog(), []AIOrderItem{
		{ProductID: "p1", Qty: 2},
		{ProductID: "p2", Qty: 1},
	})
	if err != nil {
		t.Fatalf("order sah kok ditolak: %v", err)
	}
	if len(items) != 2 {
		t.Fatalf("jumlah item = %d, mau 2", len(items))
	}
	// Price WAJIB 0 di sini — harga diisi dari DB di dalam transaksi.
	for _, it := range items {
		if it.Price != 0 {
			t.Errorf("Price untuk %s = %v, harus 0 (diisi dari DB)", it.ProductID, it.Price)
		}
	}
}

// Inti proteksi prompt-injection: produk asing harus ditolak.
func TestBuildOrderItems_RejectsForeignProduct(t *testing.T) {
	_, err := BuildOrderItems(katalog(), []AIOrderItem{
		{ProductID: "produk-bisnis-orang-lain", Qty: 1},
	})
	if !errors.Is(err, ErrProdukTidakDikenal) {
		t.Fatalf("err = %v, mau ErrProdukTidakDikenal", err)
	}
}

func TestBuildOrderItems_RejectsBadQty(t *testing.T) {
	cases := []int{0, -5, MaxQtyPerItem + 1, 999999}
	for _, qty := range cases {
		_, err := BuildOrderItems(katalog(), []AIOrderItem{{ProductID: "p1", Qty: qty}})
		if !errors.Is(err, ErrQtyTidakWajar) {
			t.Errorf("qty %d: err = %v, mau ErrQtyTidakWajar", qty, err)
		}
	}
}

// AI tidak boleh melewati batas dengan mengulang produk yang sama.
func TestBuildOrderItems_MergesDuplicatesAndRechecksLimit(t *testing.T) {
	// 60 + 60 = 120 > 100, harus ditolak walau tiap item sendiri masih sah.
	_, err := BuildOrderItems(katalog(), []AIOrderItem{
		{ProductID: "p1", Qty: 60},
		{ProductID: "p1", Qty: 60},
	})
	if !errors.Is(err, ErrQtyTidakWajar) {
		t.Fatalf("duplikat 60+60: err = %v, mau ErrQtyTidakWajar", err)
	}

	// 2 + 3 = 5, sah, dan harus digabung jadi SATU item.
	items, err := BuildOrderItems(katalog(), []AIOrderItem{
		{ProductID: "p1", Qty: 2},
		{ProductID: "p1", Qty: 3},
	})
	if err != nil {
		t.Fatalf("duplikat wajar ditolak: %v", err)
	}
	if len(items) != 1 {
		t.Fatalf("jumlah item = %d, mau 1 (digabung)", len(items))
	}
	if items[0].Quantity != 5 {
		t.Errorf("qty gabungan = %d, mau 5", items[0].Quantity)
	}
}

func TestBuildOrderItems_RejectsEmptyAndTooMany(t *testing.T) {
	if _, err := BuildOrderItems(katalog(), nil); !errors.Is(err, ErrOrderKosong) {
		t.Errorf("items nil: err = %v, mau ErrOrderKosong", err)
	}

	banyak := make([]AIOrderItem, MaxItemsPerOrder+1)
	for i := range banyak {
		banyak[i] = AIOrderItem{ProductID: "p1", Qty: 1}
	}
	if _, err := BuildOrderItems(katalog(), banyak); !errors.Is(err, ErrTerlaluBanyakItem) {
		t.Errorf("item kebanyakan: err = %v, mau ErrTerlaluBanyakItem", err)
	}
}

// Katalog kosong berarti tidak ada produk sah sama sekali.
func TestBuildOrderItems_EmptyCatalog(t *testing.T) {
	_, err := BuildOrderItems(nil, []AIOrderItem{{ProductID: "p1", Qty: 1}})
	if !errors.Is(err, ErrProdukTidakDikenal) {
		t.Fatalf("err = %v, mau ErrProdukTidakDikenal", err)
	}
}
