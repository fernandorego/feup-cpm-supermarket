package models

import (
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type User struct {
	ID               primitive.ObjectID `json:"_id,omitempty" bson:"_id,omitempty"`
	Name             string             `json:"name" validate:"required,min=3,max=100"`
	Email            string             `json:"email" validate:"email,required"`
	Password         string             `json:"password" validate:"required,min=6"`
	AccumulatedValue float64            `json:"accumulated_value"`
	IsAdmin          bool               `json:"is_admin"`
	UserImg          *string            `json:"user_img"`
	CreatedAt        time.Time          `json:"created_at"`
	UpdatedAt        time.Time          `json:"updated_at"`
}

func CreateUserFromJSON(context *gin.Context) (*User, error) {
	user := new(User)
	if err := context.BindJSON(&user); err != nil {
		return nil, err
	}
	if err := validator.New().Struct(user); err != nil {
		return nil, err
	}
	user.IsAdmin = false
	user.AccumulatedValue = 0
	user.UserImg = nil
	user.CreatedAt = time.Now()
	user.UpdatedAt = time.Now()
	return user, nil
}

type UserCredentials struct {
	Email    string `json:"email" validate:"email,required"`
	Password string `json:"password" validate:"required,min=6"`
}

func GetUserCredentialsFromJSON(context *gin.Context) (*UserCredentials, error) {
	credentials := new(UserCredentials)
	if err := context.BindJSON(&credentials); err != nil {
		return nil, err
	}
	if err := validator.New().Struct(credentials); err != nil {
		return nil, err
	}
	return credentials, nil
}
