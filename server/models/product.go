package models

import (
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
	"github.com/google/uuid"
)

type Product struct {
	UUID  uuid.UUID `json:"uuid"`
	Name  string    `json:"name" validate:"required"`
	Price float32   `json:"price" validate:"required"`
	Image string    `json:"image"`
}

func CreateProductFromJSON(context *gin.Context) (*Product, error) {
	product := new(Product)
	if err := context.BindJSON(&product); err != nil {
		return nil, err
	}
	if err := validator.New().Struct(product); err != nil {
		return nil, err
	}

	return product, nil
}
