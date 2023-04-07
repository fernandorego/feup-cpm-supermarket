package helpers

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/pem"
	"os"
)

var PrivateKey *rsa.PrivateKey

func LoadPrivateKey() error {
	privKeyString, _ := pem.Decode([]byte(os.Getenv("PRIVATE_KEY")))
	privKey, err := x509.ParsePKCS1PrivateKey(privKeyString.Bytes)
	if err != nil {
		return err
	}

	PrivateKey = privKey
	return nil
}

func LoadPublicKey(publicKey []byte) (*rsa.PublicKey, error) {
	pubKeyString, _ := pem.Decode(publicKey)
	pubKey, err := x509.ParsePKCS1PublicKey(pubKeyString.Bytes)
	if err != nil {
		return nil, err
	}

	return pubKey, nil
}

func SignMessage(message string, privateKey *rsa.PrivateKey) (string, error) {
	hash := sha256.New()
	hash.Write([]byte(message))
	hashed := hash.Sum(nil)

	signature, err := rsa.SignPKCS1v15(rand.Reader, privateKey, crypto.SHA256, hashed)
	return string(signature), err
}

func VerifySignature(message []byte, signature []byte, publicKey *rsa.PublicKey) error {
	hash := sha256.New()
	hash.Write([]byte(message))
	d := hash.Sum(nil)
	err := rsa.VerifyPKCS1v15(publicKey, crypto.SHA256, d, signature)
	if err != nil {
		return err
	}

	return nil
}

func EncryptMessage(message []byte) ([]byte, error) {
	encryptedMessage, err := rsa.EncryptPKCS1v15(rand.Reader, &PrivateKey.PublicKey, message)
	if err != nil {
		return nil, err
	}

	return encryptedMessage, nil
}

func DecryptMessage(message []byte) ([]byte, error) {
	decryptedMessage, err := rsa.DecryptPKCS1v15(rand.Reader, PrivateKey, message)
	if err != nil {
		return nil, err
	}

	return decryptedMessage, nil
}
