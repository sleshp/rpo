package ru.bmstu.pos.fclient1;

public interface TransactionEvents {
    String enterPin(int ptc, String amount);
    void transactionResult(boolean result);
}