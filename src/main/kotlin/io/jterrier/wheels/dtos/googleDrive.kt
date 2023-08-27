package io.jterrier.wheels.dtos

data class FileListDto(
    val files: List<FileDto>
)

data class FileDto(
    val id: String,
    val name: String,
)
