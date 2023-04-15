package models

import (
	"encoding/json"
	"github.com/go-playground/validator/v10"
	"github.com/google/uuid"
)

type CartProduct struct {
	ProductUUID uuid.UUID `json:"uuid"`
	Quantity    int       `json:"quantity"`
	Price       float64   `json:"price"`
	Name        *string   `json:"name"`
}

type Purchase struct {
	UUID       uuid.UUID     `json:"uuid"`
	UserUUID   uuid.UUID     `json:"user_uuid" validate:"required"`
	Cart       []CartProduct `json:"cart" validate:"required"`
	Discount   bool          `json:"discount"`
	Coupon     *uuid.UUID    `json:"coupon"`
	TotalPrice float64       `json:"total_price"`
	PaidPrice  float64       `json:"paid_price"`
	CreatedAt  string     `json:"created_at"`
}

func CreatePurchaseFromJSON(jsonPurchase string) (*Purchase, error) {
	purchase := new(Purchase)
	if err := json.Unmarshal([]byte(jsonPurchase), &purchase); err != nil {
		return nil, err
	}
	if err := validator.New().Struct(purchase); err != nil {
		return nil, err
	}
	purchase.UUID = uuid.New()
	return purchase, nil
}
