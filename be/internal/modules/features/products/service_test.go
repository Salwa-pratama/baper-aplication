package products

import (
	"context"
	"errors"
	"testing"

	"baper/internal/common/apperror"
	"baper/internal/models"

	"gorm.io/gorm"
)

// fakeRepo: dua bisnis, dua user. userA -> bizA, userB -> bizB.
type fakeRepo struct {
	businessByUser map[string]*models.Business
	products       map[string]*models.Product
	deleted        []string
	updated        []string
	created        []*models.Product
}

func newFakeRepo() *fakeRepo {
	return &fakeRepo{
		businessByUser: map[string]*models.Business{
			"userA": {ID: "bizA", UserID: "userA", Name: "Toko A"},
			"userB": {ID: "bizB", UserID: "userB", Name: "Toko B"},
		},
		products: map[string]*models.Product{
			"prodA": {ID: "prodA", BusinessID: "bizA", Name: "Kopi A", Price: 1000, Stock: 5},
			"prodB": {ID: "prodB", BusinessID: "bizB", Name: "Kopi B", Price: 2000, Stock: 5},
		},
	}
}

func (f *fakeRepo) FindBusinessByUserID(_ context.Context, userID string) (*models.Business, error) {
	b, ok := f.businessByUser[userID]
	if !ok {
		return nil, gorm.ErrRecordNotFound
	}
	return b, nil
}

func (f *fakeRepo) CreateProduct(_ context.Context, p *models.Product) error {
	f.created = append(f.created, p)
	f.products[p.ID] = p
	return nil
}

func (f *fakeRepo) FindAllProducts(_ context.Context, businessID string) ([]models.Product, error) {
	var out []models.Product
	for _, p := range f.products {
		if businessID == "" || p.BusinessID == businessID {
			out = append(out, *p)
		}
	}
	return out, nil
}

func (f *fakeRepo) FindProductByID(_ context.Context, id string) (*models.Product, error) {
	p, ok := f.products[id]
	if !ok {
		return nil, gorm.ErrRecordNotFound
	}
	return p, nil
}

func (f *fakeRepo) UpdateProduct(_ context.Context, p *models.Product) error {
	f.updated = append(f.updated, p.ID)
	return nil
}

func (f *fakeRepo) DeleteProduct(_ context.Context, id string) error {
	f.deleted = append(f.deleted, id)
	return nil
}

func appErrCode(t *testing.T, err error) int {
	t.Helper()
	var ae *apperror.AppError
	if !errors.As(err, &ae) {
		t.Fatalf("error bukan *apperror.AppError: %#v", err)
	}
	return ae.Code
}

// business_id diambil dari token, BUKAN dari body.
func TestCreateProduct_UsesBusinessFromToken(t *testing.T) {
	repo := newFakeRepo()
	svc := NewProductService(repo)

	resp, err := svc.CreateProduct(context.Background(), "userA", CreateProductRequest{
		Name:  "Produk Baru",
		Price: 5000,
		Stock: 2,
	})
	if err != nil {
		t.Fatalf("create gagal: %v", err)
	}
	if resp.BusinessID != "bizA" {
		t.Errorf("business_id = %q, mau %q (dari token userA)", resp.BusinessID, "bizA")
	}
	if len(repo.created) != 1 || repo.created[0].BusinessID != "bizA" {
		t.Errorf("produk tersimpan ke business_id yang salah: %+v", repo.created)
	}
}

// GetAll hanya boleh mengembalikan produk milik bisnis sendiri.
func TestGetAllProducts_ScopedToOwnBusiness(t *testing.T) {
	svc := NewProductService(newFakeRepo())

	list, err := svc.GetAllProducts(context.Background(), "userA")
	if err != nil {
		t.Fatalf("get all gagal: %v", err)
	}
	for _, p := range list {
		if p.BusinessID != "bizA" {
			t.Errorf("produk bisnis lain ikut terbawa: %+v", p)
		}
	}
	if len(list) != 1 || list[0].ID != "prodA" {
		t.Errorf("hasil = %+v, mau hanya prodA", list)
	}
}

// Inti temuan Lapis 2: userB tidak boleh menyentuh prodA sama sekali.
func TestOwnershipEnforced_UserBCannotTouchUserAProduct(t *testing.T) {
	repo := newFakeRepo()
	svc := NewProductService(repo)
	ctx := context.Background()

	if _, err := svc.GetProductByID(ctx, "userB", "prodA"); err == nil {
		t.Error("READ produk orang lain LOLOS")
	} else if code := appErrCode(t, err); code != 404 {
		t.Errorf("read: code = %d, mau 404 (jangan bocorkan keberadaan data)", code)
	}

	if _, err := svc.UpdateProduct(ctx, "userB", "prodA", UpdateProductRequest{Name: "Dibajak", Price: 1}); err == nil {
		t.Error("UPDATE produk orang lain LOLOS")
	} else if code := appErrCode(t, err); code != 404 {
		t.Errorf("update: code = %d, mau 404", code)
	}

	if err := svc.DeleteProduct(ctx, "userB", "prodA"); err == nil {
		t.Error("DELETE produk orang lain LOLOS")
	} else if code := appErrCode(t, err); code != 404 {
		t.Errorf("delete: code = %d, mau 404", code)
	}

	// Yang penting: tidak ada perubahan apa pun yang benar-benar terjadi.
	if len(repo.updated) != 0 {
		t.Errorf("ada update yang lolos ke repository: %v", repo.updated)
	}
	if len(repo.deleted) != 0 {
		t.Errorf("ada delete yang lolos ke repository: %v", repo.deleted)
	}
}

// Pemilik sendiri tetap harus bisa akses.
func TestOwnerCanAccessOwnProduct(t *testing.T) {
	repo := newFakeRepo()
	svc := NewProductService(repo)
	ctx := context.Background()

	if _, err := svc.GetProductByID(ctx, "userA", "prodA"); err != nil {
		t.Errorf("pemilik gagal read produknya sendiri: %v", err)
	}
	if _, err := svc.UpdateProduct(ctx, "userA", "prodA", UpdateProductRequest{Name: "Kopi A v2", Price: 1500, Stock: 7}); err != nil {
		t.Errorf("pemilik gagal update produknya sendiri: %v", err)
	}
	if err := svc.DeleteProduct(ctx, "userA", "prodA"); err != nil {
		t.Errorf("pemilik gagal hapus produknya sendiri: %v", err)
	}
	if len(repo.deleted) != 1 || repo.deleted[0] != "prodA" {
		t.Errorf("delete tidak sampai ke repository: %v", repo.deleted)
	}
}

func TestUserID_KosongDitolak(t *testing.T) {
	svc := NewProductService(newFakeRepo())

	if _, err := svc.GetAllProducts(context.Background(), ""); err == nil {
		t.Error("userID kosong LOLOS")
	} else if code := appErrCode(t, err); code != 401 {
		t.Errorf("code = %d, mau 401", code)
	}
}

func TestValidasiInput(t *testing.T) {
	svc := NewProductService(newFakeRepo())
	ctx := context.Background()

	cases := []CreateProductRequest{
		{Name: "", Price: 100},             // nama kosong
		{Name: "X", Price: 0},              // harga 0
		{Name: "X", Price: -5},             // harga negatif
		{Name: "X", Price: 100, Stock: -1}, // stok negatif
	}
	for i, c := range cases {
		if _, err := svc.CreateProduct(ctx, "userA", c); err == nil {
			t.Errorf("case %d (%+v) LOLOS padahal tidak valid", i, c)
		} else if code := appErrCode(t, err); code != 400 {
			t.Errorf("case %d: code = %d, mau 400", i, code)
		}
	}
}

// User yang belum punya bisnis harus dapat 404, bukan panic.
func TestUserTanpaBisnis(t *testing.T) {
	svc := NewProductService(newFakeRepo())

	if _, err := svc.GetAllProducts(context.Background(), "userTanpaBisnis"); err == nil {
		t.Error("user tanpa bisnis LOLOS")
	} else if code := appErrCode(t, err); code != 404 {
		t.Errorf("code = %d, mau 404", code)
	}
}
