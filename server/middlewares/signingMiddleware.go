package middlewares

import (
	"server/helpers"
	"server/models"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func VerifySignature(context *gin.Context) {

	b64MessageSignatureString := context.GetHeader("Signature")
	if b64MessageSignatureString == "" {
		helpers.SetStatusBadRequest(context, "missing signature header")
		context.Abort()
		return
	}

	var b64SignedMessageString string

	if helpers.MethodWithBody(context.Request.Method) {
		signedBody, err := models.CreateSignedMessageFromContext(context)
		if err != nil {
			helpers.SetStatusBadRequest(context, "error parsing signed message: "+err.Error())
			context.Abort()
			return
		}

		b64SignedMessageString = signedBody.B64MessageString

	} else {
		b64SignedMessageString = context.GetHeader("Signed-Message")
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

	err = helpers.VerifySignature(b64SignedMessageString, b64MessageSignatureString, pubKey)
	if err != nil {
		helpers.SetStatusUnauthorized(context, "error signing message: "+err.Error())
		context.Abort()
		return
	}

	context.Next()
}
