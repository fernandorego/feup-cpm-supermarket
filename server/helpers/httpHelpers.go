package helpers

import "net/http"

func MethodWithBody(method string) bool {
	switch method {
	case
		http.MethodPost,
		http.MethodPut,
		http.MethodPatch:
		return true
	}
	return false
}
