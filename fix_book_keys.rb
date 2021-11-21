require 'json'
require 'pathname'

gem 'sqlite3'
require 'sqlite3'

key_mapping = JSON.parse(Pathname.new("key_mapping.json").read)

db = SQLite3::Database.new("Manga.db")
db_audit = SQLite3::Database.new("MangaAudit.db")

key_mapping.each_pair do |old_key, key|
  old_book_id = old_key[0..14].to_i(16)
  book_id = key[0..14].to_i(16)

  query = "UPDATE books_metadata SET book_id=? WHERE book_id=?"
  values = [book_id, old_book_id]
  db.execute(query, values)

  query = "UPDATE lists_books SET book_id=? WHERE book_id=?"
  values = [book_id, old_book_id]
  db.execute(query, values)

  query = "UPDATE audit_events SET resource_id=? WHERE resource_id=?"
  values = [book_id, old_book_id]
  db_audit.execute(query, values)
end