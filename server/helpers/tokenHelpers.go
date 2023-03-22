package helpers

import (
	"errors"
	"os"
	"time"

	"github.com/golang-jwt/jwt"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type SignedDetails struct {
	UserID  primitive.ObjectID
	IsAdmin bool
	jwt.StandardClaims
}

func GenerateToken(userID primitive.ObjectID, isAdmin bool) (string, error) {
	claims := SignedDetails{
		UserID:  userID,
		IsAdmin: isAdmin,
		StandardClaims: jwt.StandardClaims{
			ExpiresAt: time.Now().Add(time.Hour * 24).Unix(),
		},
	}
	return jwt.NewWithClaims(jwt.SigningMethodHS256, claims).SignedString([]byte(os.Getenv("SECRET_KEY")))
}

func ValidateToken(tokenString string) (*SignedDetails, error) {
	token, err := jwt.ParseWithClaims(tokenString, &SignedDetails{}, func(token *jwt.Token) (interface{}, error) {
		return []byte(os.Getenv("SECRET_KEY")), nil
	})
	if err != nil {
		return nil, err
	}
	if claims, ok := token.Claims.(*SignedDetails); !ok || !token.Valid {
		return nil, errors.New("the token is invalid")
	} else {
		return claims, nil
	}
}
