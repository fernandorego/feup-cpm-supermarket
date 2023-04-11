package models

import (
	"server/db"
	"time"

	"github.com/google/uuid"
	"golang.org/x/net/context"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type Card struct {
	CardNumber string `json:"card_number" validate:"required,min=16,max=16"`
	CardCVV    string `json:"card_cvv" validate:"required,min=3,max=3"`
	CardDate   string `json:"card_date" validate:"required"`
}

type User struct {
	ID               primitive.ObjectID `json:"_id,omitempty" bson:"_id,omitempty"`
	UUID             uuid.UUID          `json:"uuid"`
	Nickname         string             `json:"nickname" validate:"required,min=3"`
	Password         string             `json:"password" validate:"required,min=6"`
	Name             string             `json:"name" validate:"required,min=3,max=100"`
	Card             Card               `json:"card" validate:"required"`
	PublicKey        string             `json:"public_key" validate:"required"`
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

	// TODO:
	// Admins should be created using another endopoint with some
	// security check
	user.IsAdmin = user.IsAdmin || false
	user.AccumulatedValue = 0
	user.UserImg = nil
	user.CreatedAt = time.Now()
	user.UpdatedAt = time.Now()
	return user, nil
}

type UserCredentials struct {
	Nickname  string `json:"nickname" validate:"required,min=3"`
	Password  string `json:"password" validate:"required,min=6"`
	PublicKey string `json:"public_key" validate:"required"`
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

func GetUserFromID(id primitive.ObjectID) (user *User, err error) {
	database := db.GetDatabase()
	usersCollection := database.Collection("users")

	ctx := context.Background()
	user = new(User)
	if err := usersCollection.FindOne(ctx, bson.M{"_id": id}).Decode(&user); err != nil {
		return nil, err
	}

	return user, nil
}
