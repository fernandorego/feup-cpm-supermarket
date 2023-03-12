package helpers

import (
	"fmt"
	"os"
	"time"

	"github.com/golang-jwt/jwt"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type SignedDetails struct {
	CreatedAt  time.Time
	Authorized bool
	UserID     string
	jwt.StandardClaims
}

func GenerateToken(userID primitive.ObjectID) (string, error) {
	claims := SignedDetails{
		CreatedAt:  time.Now(),
		Authorized: true,
		UserID:     userID.Hex(),
		StandardClaims: jwt.StandardClaims{
			ExpiresAt: time.Now().Add(time.Hour * 24).Unix(),
		},
	}
	return jwt.NewWithClaims(jwt.SigningMethodHS256, claims).SignedString([]byte(os.Getenv("SECRET_KEY")))
}

func ValidateToken(tokenString string) (*SignedDetails, string) {
	token, err := jwt.ParseWithClaims(tokenString, &SignedDetails{}, func(token *jwt.Token) (interface{}, error) {
		return []byte(os.Getenv("SECRET_KEY")), nil
	})
	fmt.Println("cheguei0")
	if err != nil {
		return &SignedDetails{}, err.Error()
	}
	claims, ok := token.Claims.(*SignedDetails)
	if !ok {
		return &SignedDetails{}, "the token is invalid"
	}
	if claims.ExpiresAt < time.Now().Unix() {
		return &SignedDetails{}, "the token is expired"
	}
	return claims, ""
}
