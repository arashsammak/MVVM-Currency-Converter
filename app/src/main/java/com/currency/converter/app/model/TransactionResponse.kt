package com.currency.converter.app.model

import com.currency.converter.app.data.db.TransactionEntity
class TransactionResponse(var isCommitted: Boolean, var transactionEntity: TransactionEntity)