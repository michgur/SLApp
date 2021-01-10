package com.klmn.slapp.data.firestore.entities

import com.google.firebase.Timestamp
import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList

object ListFirestoreMapper : EntityModelMapper<FirestoreEntities.SList, SlappList> {
    override fun toEntity(model: SlappList) = FirestoreEntities.SList(
        model.id,
        model.name,
        model.user,
        Timestamp(model.timestamp, 0),
        model.users,
        model.items.map {
            FirestoreEntities.Item(
                it.name,
                it.user,
                Timestamp(it.timestamp, 0)
            )
        }
    )

    override fun toModel(entity: FirestoreEntities.SList) = SlappList(
        entity.id,
        entity.name,
        entity.created_by,
        entity.timestamp.seconds,
        entity.items.map {
            SlappItem(
                it.name,
                it.user_id,
                it.timestamp.seconds
            )
        }.toMutableList(),
        entity.users
    )
}