package com.syntax_checker;
public class SyntaxErrorException extends Exception {
    public SyntaxErrorException(String message) {
        super(message);
    }
}