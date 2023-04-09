package models

import (
	"github.com/gin-gonic/gin"
	"github.com/gin-gonic/gin/binding"
	"github.com/go-playground/validator/v10"
)

type SignedMessage struct {
	B64MessageString string `json:"b64MessageString" validate:"required"`
}

func CreateSignedMessageFromContext(context *gin.Context) (*SignedMessage, error) {
	signedMessage := new(SignedMessage)
	if err := context.ShouldBindBodyWith(&signedMessage, binding.JSON); err != nil {
		return nil, err
	}
	if err := validator.New().Struct(signedMessage); err != nil {
		return nil, err
	}
	return signedMessage, nil
}
