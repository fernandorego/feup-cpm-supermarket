package models

import (
	"encoding/json"

	"github.com/go-playground/validator/v10"
	"github.com/google/uuid"
)

type Product struct {
	UUID  uuid.UUID `json:"uuid"`
	Name  string    `json:"name" validate:"required"`
	Price float32   `json:"price" validate:"required"`
	Image string    `json:"image"`
}

func CreateProductFromJSONString(jsonProduct string) (*Product, error) {
	product := new(Product)
	if err := json.Unmarshal([]byte(jsonProduct), &product); err != nil {
		return nil, err
	}
	if err := validator.New().Struct(product); err != nil {
		return nil, err
	}

	return product, nil
}
