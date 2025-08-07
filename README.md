<div className="book-grid">
      {books.map((book) => (
        <div key={book.id} className="book-card">
          <div className="book-id">#{book.id}</div>
          <div className="book-name">{book.name}</div>
        </div>
      ))}
    </div>
