package com.klmn.slapp.data.firestore.entities

import com.google.firebase.Timestamp
import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.domain.SlappItem

object ItemFirestoreMapper : EntityModelMapper<FirestoreEntities.Item, SlappItem> {
    override fun toEntity(model: SlappItem) = FirestoreEntities.Item(
        model.name,
        model.user,
        Timestamp(model.timestamp, 0)
    )

    override fun toModel(entity: FirestoreEntities.Item) = SlappItem(
        entity.name,
        entity.user_id,
        entity.timestamp.seconds
    )
}