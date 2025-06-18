package ru.job4j.bmb.exception;

public class SentContentException extends RuntimeException {
  public SentContentException(String message, Throwable throwable) {
    super(message, throwable);
  }
}