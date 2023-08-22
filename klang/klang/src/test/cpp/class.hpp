
template <typename T>
class Complex {
private:
    T real;
    T imag;

public:
    Complex();
    Complex(T r, T i);

    T getReal() const;
    T getImaginary() const;

    Complex<T> operator+(const Complex<T>& other) const;
    Complex<T> operator-(const Complex<T>& other) const;
    Complex<T> operator*(const Complex<T>& other) const;
    Complex<T> operator/(const Complex<T>& other) const;

    bool operator==(const Complex<T>& other) const;
    bool operator!=(const Complex<T>& other) const;

};