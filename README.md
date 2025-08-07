import React, { useEffect, useState } from "react";
import "./BookGrid.css";

const BookGrid = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true); // For loader
  const [error, setError] = useState(null);     // For error handling

  useEffect(() => {
    const fetchBooks = async () => {
      try {
        const response = await fetch("https://your-api-endpoint.com/api/books"); // 🔁 Replace with your real API URL
        if (!response.ok) {
          throw new Error("Failed to fetch books");
        }
        const data = await response.json();
        setBooks(data);
      } catch (err) {
        setError(err.message || "Something went wrong");
      } finally {
        setLoading(false);
      }
    };

    fetchBooks();
  }, []);

  if (loading) return <p style={{ padding: 20 }}>Loading book data...</p>;
  if (error) return <p style={{ padding: 20, color: "red" }}>{error}</p>;

  return (
    <div className="book-grid-container">
      {books.map((book) => (
        <div key={book.id} className="book-card">
          <h3>{book.name}</h3>
          <p><strong>Bonds:</strong> {book.bonds}</p>
          <p><strong>Total Face Value:</strong> ${book.faceValue.toLocaleString()}</p>
          <p><strong>Region:</strong> {book.region}</p>
        </div>
      ))}
    </div>
  );
};

export default BookGrid;
