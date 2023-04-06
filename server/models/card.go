package models

type Card struct {
	CardNumber string `json:"card_number" validate:"required,min=16,max=16"`
	CardCVV    string `json:"card_cvv" validate:"required,min=3,max=3"`
	CardDate   string `json:"card_date" validate:"required"`
}
