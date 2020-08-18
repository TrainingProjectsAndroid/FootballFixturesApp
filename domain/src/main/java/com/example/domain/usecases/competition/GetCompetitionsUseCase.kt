package com.example.domain.usecases.competition

import com.example.domain.entities.DomainEntities.*
import com.example.domain.repository.CompetitionsRepository
import javax.inject.Inject

class GetCompetitionsUseCase @Inject constructor(
    private val competitionsRepository: CompetitionsRepository
) {

    operator fun invoke(): List<DomainCompetitions> {
        return competitionsRepository.getAllCompetitionsFromDb()
    }

}