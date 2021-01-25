package com.klmn.slapp.data.firestore.entities

import com.google.firebase.Timestamp
import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.data.contacts.ContactProvider
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList

class FirestoreListMapper(
    private val contactProvider: ContactProvider
) : EntityModelMapper<FirestoreEntities.SList, SlappList> {
    override fun toEntity(model: SlappList) = FirestoreEntities.SList(
        model.id,
        model.name,
        model.user.phoneNumber,
        Timestamp(model.timestamp, 0),
        model.users.map(Contact::phoneNumber),
        model.items.map {
            FirestoreEntities.Item(
                it.name,
                it.user.phoneNumber,
                Timestamp(it.timestamp, 0)
            )
        },
        model.isNew
    )

    override fun toModel(entity: FirestoreEntities.SList) = SlappList(
        entity.id,
        entity.name,
        getContact(entity.created_by),
        entity.timestamp.seconds,
        entity.items.map {
            SlappItem(
                it.name,
                getContact(it.user_id),
                it.timestamp.seconds
            )
        }.toMutableList(),
        entity.users.map(::getContact),
        entity.isNew
    )

    private fun getContact(number: String) = contactProvider.getContact(number) ?: Contact(number)
}