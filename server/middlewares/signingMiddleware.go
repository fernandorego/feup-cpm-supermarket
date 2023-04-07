package middlewares

import (
	"crypto"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/pem"
	"server/helpers"
	"server/models"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func VerifySignature(context *gin.Context) {
	encodedJSON, exists := context.Get("hash")
	if !exists {
		helpers.SetStatusBadRequest(context, "missing string encoded JSON object")
		return
	}

	signature, exists := context.Get("signature")
	if !exists {
		helpers.SetStatusBadRequest(context, "missing string encoded signature")
		return
	}

	user, err := models.GetUserFromID(context.MustGet("user_id").(primitive.ObjectID))
	if err != nil {
		helpers.SetStatusInternalServerError(context, "couldn't retreive user")
		return
	}

	pubKeyString, _ := pem.Decode([]byte(user.PublicKey))
	pubKey, err := x509.ParsePKCS1PublicKey(pubKeyString.Bytes)
	if err != nil {
		helpers.SetStatusInternalServerError(context, "couldn't parse user pubkey")
		return
	}
	hash := sha256.New()
	hash.Write([]byte(encodedJSON.(string)))
	d := hash.Sum(nil)
	err = rsa.VerifyPKCS1v15(pubKey, crypto.SHA256, d, []byte(signature.(string)))
	if err != nil {
		helpers.SetStatusBadRequest(context, "signatures don't match")
		return
	}

	context.Next()
}
