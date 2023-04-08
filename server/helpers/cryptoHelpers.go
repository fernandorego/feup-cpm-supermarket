package helpers

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"os"
)

/*
var PrivateKey *rsa.PrivateKey

	func LoadServerPrivateKey() error {
		privKeyString, _ := pem.Decode([]byte(os.Getenv("PRIVATE_KEY")))
		privKey, err := x509.ParsePKCS1PrivateKey(privKeyString.Bytes)
		if err != nil {
			return err
		}

		PrivateKey = privKey
		return nil
	}
*/
var PublicKey *rsa.PublicKey

func LoadServerPublicKey() error {
	publicKeyString, _ := pem.Decode([]byte(os.Getenv("PUBLIC_KEY")))
	publicKey, err := x509.ParsePKIXPublicKey(publicKeyString.Bytes)
	if err != nil {
		return err
	}

	PublicKey = publicKey.(*rsa.PublicKey)
	return nil
}

func EncryptMessage(message []byte) ([]byte, error) {
	encryptedMessage, err := rsa.EncryptPKCS1v15(rand.Reader, PublicKey, message)
	if err != nil {
		return nil, err
	}

	return encryptedMessage, nil
}

/*
func SignMessage(message string, privateKey *rsa.PrivateKey) (string, error) {
	hash := sha256.New()
	hash.Write([]byte(message))
	hashed := hash.Sum(nil)

	signature, err := rsa.SignPKCS1v15(rand.Reader, privateKey, crypto.SHA256, hashed)
	return string(signature), err
}*/

func ParseClientPublicKey(publicKey string) (*rsa.PublicKey, error) {
	pubKey, err := x509.ParsePKIXPublicKey([]byte(publicKey))
	if err != nil {
		return nil, err
	}

	return pubKey.(*rsa.PublicKey), nil
}

func VerifySignature(message string, signature string, publicKey *rsa.PublicKey) error {
	decodedSignature, err := base64.StdEncoding.DecodeString(signature)
	if err != nil {
		return err
	}

	hasher := sha256.New()
	hasher.Write([]byte(message))
	digest := hasher.Sum(nil)

	err = rsa.VerifyPKCS1v15(publicKey, crypto.SHA256, digest, decodedSignature)
	if err != nil {
		return err
	}

	return nil
}

/*
func DecryptMessage(message []byte) ([]byte, error) {
	decryptedMessage, err := rsa.DecryptPKCS1v15(rand.Reader, PrivateKey, message)
	if err != nil {
		return nil, err
	}

	return decryptedMessage, nil
}
*/
