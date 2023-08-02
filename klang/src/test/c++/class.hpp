#ifndef COMPLEX_H
#define COMPLEX_H

#include <iostream>

template <typename T>
class Complex {
private:
    T real;
    T imag;

public:
    Complex() : real(0), imag(0) {}
    Complex(T r, T i) : real(r), imag(i) {}

    T getReal() const { return real; }
    T getImaginary() const { return imag; }

    Complex<T> operator+(const Complex<T>& other) const {
        return Complex<T>(real + other.real, imag + other.imag);
    }

    Complex<T> operator-(const Complex<T>& other) const {
        return Complex<T>(real - other.real, imag - other.imag);
    }

    Complex<T> operator*(const Complex<T>& other) const {
        return Complex<T>(real * other.real - imag * other.imag, real * other.imag + imag * other.real);
    }

    Complex<T> operator/(const Complex<T>& other) const {
        T denominator = other.real * other.real + other.imag * other.imag;
        return Complex<T>((real * other.real + imag * other.imag) / denominator, (imag * other.real - real * other.imag) / denominator);
    }

    bool operator==(const Complex<T>& other) const {
        return (real == other.real) && (imag == other.imag);
    }

    bool operator!=(const Complex<T>& other) const {
        return !(*this == other);
    }

    friend std::ostream& operator<<(std::ostream& os, const Complex<T>& complex) {
        os << complex.real << " + " << complex.imag << "i";
        return os;
    }
};

#endif // COMPLEX_H
