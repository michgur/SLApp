package com.klmn.slapp.data.room.entities

import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.domain.SlappList

object ListEntityMapper : EntityModelMapper<RoomEntities.SList, Pair<SlappList, EntityState>> {
    override fun toEntity(model: Pair<SlappList, EntityState>) = TODO("no longer using ROOM")
    override fun toModel(entity: RoomEntities.SList) = TODO("no longer using ROOM")
}