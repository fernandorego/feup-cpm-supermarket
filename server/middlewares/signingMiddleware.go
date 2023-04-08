package middlewares

import (
	"server/helpers"
	"server/models"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func VerifySignature(context *gin.Context) {
	signedMessage, err := models.CreateSignedMessageFromJSONString(context)
	if err != nil {
		helpers.SetStatusBadRequest(context, "error parsing signed message: "+err.Error())
		context.Abort()
		return
	}

	user, err := models.GetUserFromID(context.MustGet("user_id").(primitive.ObjectID))
	if err != nil {
		helpers.SetStatusInternalServerError(context, err.Error())
		context.Abort()
		return
	}

	pubKey, err := helpers.ParseClientPublicKey(user.PublicKey)
	if err != nil {
		helpers.SetStatusInternalServerError(context, "error loading public key: "+err.Error())
		context.Abort()
		return
	}

	err = helpers.VerifySignature(signedMessage.B64MessageString, signedMessage.B64SignatureString, pubKey)
	if err != nil {
		helpers.SetStatusUnauthorized(context, "error signing message: "+err.Error())
		context.Abort()
		return
	}

	context.Next()
}
