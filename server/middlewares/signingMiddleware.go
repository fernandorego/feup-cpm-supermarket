package middlewares

import (
	"encoding/base64"
	"server/helpers"
	"server/models"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func VerifySignature(context *gin.Context) {
	b64Message, exists := context.Get("b64Message")
	if !exists {
		helpers.SetStatusBadRequest(context, "missing string encoded JSON object")
		return
	}

	b64Signature, exists := context.Get("b64Signature")
	if !exists {
		helpers.SetStatusBadRequest(context, "missing string encoded signature")
		return
	}

	signature := make([]byte, base64.StdEncoding.DecodedLen(len(b64Signature.([]byte))))
	_, err := base64.StdEncoding.Decode(signature, b64Signature.([]byte))
	if err != nil {
		helpers.SetStatusBadRequest(context, "invalid signature encoding")
		return
	}

	user, err := models.GetUserFromID(context.MustGet("user_id").(primitive.ObjectID))
	if err != nil {
		helpers.SetStatusInternalServerError(context, "couldn't retreive user")
		return
	}

	pubKey, err := helpers.LoadPublicKey([]byte(user.PublicKey))
	if err != nil {
		helpers.SetStatusInternalServerError(context, "couldn't load public key")
		return
	}

	err = helpers.VerifySignature(b64Message.([]byte), signature, pubKey)
	if err != nil {
		helpers.SetStatusUnauthorized(context, "invalid signature")
		return
	}

	context.Next()
}
