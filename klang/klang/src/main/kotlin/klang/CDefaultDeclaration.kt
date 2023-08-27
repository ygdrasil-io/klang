package klang

import klang.domain.VoidType

fun DeclarationRepository.insertCDefaultDeclaration() {
	save(VoidType)
}