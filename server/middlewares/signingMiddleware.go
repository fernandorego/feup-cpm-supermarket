package middlewares

import (
	"encoding/base64"
	"encoding/json"
	"errors"
	"github.com/google/uuid"
	"server/helpers"
	"server/models"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func VerifyOwnSignature(context *gin.Context) {

	b64MessageSignatureString, b64SignedMessageString, err := extractSignatureAndPayload(context)
	if err != nil {
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

	err = helpers.VerifySignature(b64SignedMessageString, b64MessageSignatureString, pubKey)
	if err != nil {
		helpers.SetStatusUnauthorized(context, "error signing message: "+err.Error())
		context.Abort()
		return
	}

	context.Next()
}

func VerifyOthersSignature(context *gin.Context) {
	b64MessageSignatureString, b64SignedMessageString, err := extractSignatureAndPayload(context)
	if err != nil {
		return
	}

	signedMessage, err := models.CreateSignedMessageFromContext(context)
	if err != nil {
		helpers.SetStatusBadRequest(context, "error parsing signed message")
		return
	}

	messageBytes, err := base64.StdEncoding.Strict().DecodeString(signedMessage.B64MessageString)
	if err != nil {
		helpers.SetStatusBadRequest(context, "error decoding message: "+err.Error())
		return
	}

	var jsonObj map[string]interface{}
	err = json.Unmarshal(messageBytes, &jsonObj)
	if err != nil {
		helpers.SetStatusBadRequest(context, "Cannot find \"user_uuid\": "+err.Error())
		context.Abort()
		return
	}

	userUUID, err := uuid.Parse(jsonObj["user_uuid"].(string))
	if err != nil {
		helpers.SetStatusBadRequest(context, "Cannot parse \"user_uuid\": "+err.Error())
		context.Abort()
		return
	}

	user, err := models.GetUserFromUUID(userUUID)
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

func extractSignatureAndPayload(context *gin.Context) (string, string, error) {
	b64MessageSignatureString := context.GetHeader("Signature")
	if b64MessageSignatureString == "" {
		helpers.SetStatusBadRequest(context, "missing signature header")
		context.Abort()
		return "", "", errors.New("missing signature header")
	}

	var b64SignedMessageString string

	if helpers.MethodWithBody(context.Request.Method) {
		signedBody, err := models.CreateSignedMessageFromContext(context)
		if err != nil {
			helpers.SetStatusBadRequest(context, "error parsing signed message: "+err.Error())
			context.Abort()
			return "", "", err
		}

		b64SignedMessageString = signedBody.B64MessageString

	} else {
		b64SignedMessageString = context.GetHeader("Signed-Message")
	}

	return b64MessageSignatureString, b64SignedMessageString, nil
}
