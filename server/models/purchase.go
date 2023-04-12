package models

import (
	"encoding/json"
	"github.com/go-playground/validator/v10"
	"github.com/google/uuid"
)

type CartProduct struct {
	ProductUUID uuid.UUID `json:"uuid"`
	Quantity    int       `json:"quantity"`
	Name        *string   `json:"name"`
}

type Purchase struct {
	UUID     uuid.UUID     `json:"uuid"`
	UserUUID uuid.UUID     `json:"user_uuid" validate:"required"`
	Cart     []CartProduct `json:"cart" validate:"required"`
	Discount bool          `json:"discount"`
	Coupon   *uuid.UUID    `json:"coupon"`
}

func CreatePurchaseFromJSON(jsonPurchase string) (*Purchase, error) {
	purchase := new(Purchase)
	if err := json.Unmarshal([]byte(jsonPurchase), &purchase); err != nil {
		return nil, err
	}
	if err := validator.New().Struct(purchase); err != nil {
		return nil, err
	}
	return purchase, nil
}
