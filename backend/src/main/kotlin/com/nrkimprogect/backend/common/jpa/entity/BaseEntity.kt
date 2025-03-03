package com.nrkimprogect.backend.common.jpa.entity

import jakarta.persistence.*
import org.springframework.data.domain.Persistable

@MappedSuperclass
abstract class BaseEntity :Persistable<Long> {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val _id: Long? = null

    override fun getId(): Long? = _id

    override fun isNew(): Boolean = _id == null || id == 0L
}