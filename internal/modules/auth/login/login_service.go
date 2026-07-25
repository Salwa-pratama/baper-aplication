package login

import "errors"


type Service interface {
	SignIn() (string, error)
}


type service struct {
	repo Repository
}


func NewLoginService(repo Repository) Service {
	return  &service{repo}
}


// Service Implement Function
func (s *service) SignIn() (string, error) {
	say_hay,err := s.repo.SayHay("Ananda")

	if err!= nil {
		return "", errors.New("API tidak ditemukan")
	}

	return say_hay, nil

}
