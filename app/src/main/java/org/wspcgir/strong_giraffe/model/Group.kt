package org.wspcgir.strong_giraffe.model

data class Group<T> (
    val first: T,
    val rest: List<T>
) {
    fun <R> innerGroup(by: (T) -> R): Group<Group<T>> {
        val items = listOf(first).plus(rest)
        val groups = fromList(items, by)
        return Group(groups.first(), groups.drop(1))
    }

    companion object {
        fun <T, R> fromList(items: List<T>, by: (T) -> R): List<Group<T>> {
            val pair = items
                .fold(Pair(emptyList<List<T>>(), emptyList<T>())) { acc, x ->
                    if (acc.second.isEmpty()) {
                        Pair(acc.first, listOf(x))
                    } else if (by(acc.second.first()) == by(x)) {
                        Pair(acc.first, acc.second.plus(x))
                    } else {
                        Pair(acc.first.plus(element = acc.second), listOf(x))
                    }
                }
            val groups = pair.first.plus(element = pair.second)
            return groups.mapNotNull { group ->
                if (group.isNotEmpty()) {
                    val first = group.first()
                    Group(first, group.drop(1))
                } else {
                    null
                }
            }
        }
    }
}