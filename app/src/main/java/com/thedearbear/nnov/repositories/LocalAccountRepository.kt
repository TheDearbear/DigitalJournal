package com.thedearbear.nnov.repositories

import com.thedearbear.nnov.AppDatabase
import com.thedearbear.nnov.account.LocalAccount

class LocalAccountRepository(
    private val database: AppDatabase
) {
    fun getAll(): List<LocalAccount> {
        return database.accountDao().getAll()
    }

    fun findById(id: Int): LocalAccount? {
        return database.accountDao().getById(id)
    }

    fun add(account: LocalAccount) {
        database.accountDao().insertAll(account)
    }

    fun update(account: LocalAccount) : LocalAccount? {
        val old = findById(account.id)

        if (old != null) {
            account.name = old.name

            database.accountDao().update(account)

            return account
        }

        return null
    }

    fun delete(id: Int) {
        database.accountDao().delete(id)
    }
}
