package helpers

import (
	"os"
	"time"

	"github.com/golang-jwt/jwt"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func GenerateToken(userID primitive.ObjectID) (string, error) {
	claims := jwt.MapClaims{}
	claims["created_at"] = time.Now()
	claims["expires"] = time.Hour * 24
	claims["authorized"] = true
	claims["user_id"] = userID.Hex()
	return jwt.NewWithClaims(jwt.SigningMethodHS256, claims).SignedString([]byte(os.Getenv("SECRET_KEY")))
}
