package com.klmn.slapp.data.firestore.entities

import com.google.firebase.Timestamp
import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.data.contacts.ContactProvider
import com.klmn.slapp.domain.Contact
import com.klmn.slapp.domain.SlappItem

class FirestoreItemMapper(
    private val contactProvider: ContactProvider
) : EntityModelMapper<FirestoreEntities.Item, SlappItem> {
    override fun toEntity(model: SlappItem) = FirestoreEntities.Item(
        model.name,
        model.user.phoneNumber,
        Timestamp(model.timestamp, 0)
    )

    override fun toModel(entity: FirestoreEntities.Item) = SlappItem(
        entity.name,
        contactProvider.getContact(entity.user_id) ?: Contact(entity.user_id),
        entity.timestamp.seconds
    )
}