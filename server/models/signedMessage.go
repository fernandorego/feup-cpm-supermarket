package models

import (
	"github.com/gin-gonic/gin"
	"github.com/gin-gonic/gin/binding"
	"github.com/go-playground/validator/v10"
)

type SignedMessage struct {
	B64SignatureString string `json:"b64SignatureString" validate:"required"`
	Message            string `json:"message" validate:"required"`
}

func CreateSignedMessageFromJSONString(context *gin.Context) (*SignedMessage, error) {
	signedMessage := new(SignedMessage)
	if err := context.ShouldBindBodyWith(&signedMessage, binding.JSON); err != nil {
		return nil, err
	}
	if err := validator.New().Struct(signedMessage); err != nil {
		return nil, err
	}
	return signedMessage, nil
}
