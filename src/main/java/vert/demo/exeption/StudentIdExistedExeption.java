package vert.demo.exeption;

public class StudentIdExistedExeption extends RuntimeException {
    @Override
    public String getMessage() {
        return "Sudent ID has Existed";
    }
}
