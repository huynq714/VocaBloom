# VocaBloom – Firebase schema & defense notes

Firestore là NoSQL: collection ≠ SQL table, document ID ≠ foreign key enforced by DB.

| Collection | Document key | Core fields | Relationship |
|---|---|---|---|
| `users` | Firebase Authentication UID | `userId`, `dailyGoal`, `updatedAt` | One user has N progress and N review logs |
| `topics` | `t001`... | `title`, `description`, `sortOrder` | One topic has N words |
| `words` | `w001`... | `topicId`, `english`, `phonetic`, `meaning`, `example`, `order` | One word has N user progress and N review logs |
| `userProgress` | `{uid}_{wordId}` | `userId`, `wordId`, `level`, `nextReviewAt`, `lastReviewedAt` | Joins user and word logically (N:N resolved to 2 x 1:N) |
| `reviewLogs` | auto-ID | `userId`, `wordId`, `rating`, `reviewedAt`, `levelAfter` | One user-word pair has multiple review events |

## Why each collection?
- `users`: profile and daily target by account. Credentials are in Firebase Auth, not here.
- `topics`: a single editable catalogue of subject headings, avoids duplicating category labels inside every word.
- `words`: shared learning content, not contaminated by student-specific recall levels.
- `userProgress`: one current state per user-word pair; the due-time query feeds spaced repetition.
- `reviewLogs`: immutable event history so progress isn't confused with historical review statistics.

## Interactions
1. Topic selected -> filter words by `topicId`.
2. Card rated -> update user's progress, create one review log event.
3. Home -> count progress where `nextReviewAt <= now`.
4. Stats -> aggregate progress levels and logs for the signed-in user.

## Deliberate starter limits
- App reads 30 bundled vocabulary words locally for reliable offline demo; Firebase seed script populates the `topics` and `words` collections for examination.
- User progress is persisted locally. When configured, rating actions mirror current progress plus logs to Firestore using an anonymous UID. The app does **not yet download progress from another device or guarantee atomic multi-document sync**. A production release requires retry queue / transaction or idempotent event IDs, login account and cloud readback.
- Firebase authorization rules ensure users can modify only their own progress. Rules included in `notes/FIRESTORE_RULES.txt`.
- Timestamp fields in Firestore are true timestamp values, not display strings.

## Demo record
Topic `t001` -> Word `w001` (Beverage) -> progress `${uid}_w001` (level 2, due in 3 days) -> event in reviewLogs after tapping **Nhớ**.
