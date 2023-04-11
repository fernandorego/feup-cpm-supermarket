package models

import "github.com/google/uuid"

type CartProduct struct {
	ProductUUID uuid.UUID `json:"product_uuid"`
	Quantity    int       `json:"quantity"`
}

type Purchase struct {
	UUID     uuid.UUID     `json:"uuid"`
	Cart     []CartProduct `json:"cart" validate:"required"`
	Discount bool          `json:"discount" validate:"required"`
	Coupon   uuid.UUID     `json:"coupon" validate:"required"`
}
