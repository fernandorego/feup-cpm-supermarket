package models

import (
	"go.mongodb.org/mongo-driver/bson/primitive"
	"time"
)

type User struct {
	ID        primitive.ObjectID `json:"id"`
	Name      *string            `json:"name" validate:"required, min=2, max=100"`
	Email     *string            `json:"email" validate:"email, required"`
	Password  *string            `json:"password" validate:"required, min=6"`
	CreatedAt time.Time          `json:"created_at"`
	UpdatedAt time.Time          `json:"updated_at"`
}
