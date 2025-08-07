import React from "react";
import "./BookGrid.css";

const mockBooks = [
  {
    id: 1,
    name: "Corporate Bonds - Asia",
    bonds: 12,
    faceValue: 1_200_000,
    region: "Singapore",
  },
  {
    id: 2,
    name: "Govt Bonds - EU",
    bonds: 18,
    faceValue: 2_700_000,
    region: "Germany",
  },
  {
    id: 3,
    name: "Municipal Bonds - US",
    bonds: 10,
    faceValue: 950_000,
    region: "New York",
  },
  {
    id: 4,
    name: "Sovereign Bonds - MEA",
    bonds: 7,
    faceValue: 1_150_000,
    region: "UAE",
  },
  {
    id: 5,
    name: "High Yield - LATAM",
    bonds: 9,
    faceValue: 820_000,
    region: "Brazil",
  },
  {
    id: 6,
    name: "Green Bonds",
    bonds: 5,
    faceValue: 620_000,
    region: "Global",
  },
  {
    id: 7,
    name: "Treasury - US",
    bonds: 20,
    faceValue: 3_000_000,
    region: "USA",
  },
  {
    id: 8,
    name: "Retail Bonds - UK",
    bonds: 6,
    faceValue: 700_000,
    region: "UK",
  },
  {
    id: 9,
    name: "Private Bonds - Africa",
    bonds: 4,
    faceValue: 560_000,
    region: "South Africa",
  },
];

const BookGrid = () => {
  return (
    <div className="book-grid-container">
      {mockBooks.map((book) => (
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



.book-grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  padding: 20px;
}

.book-card {
  background-color: #f5f5f5;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.book-card h3 {
  margin-bottom: 12px;
  font-size: 1.2rem;
  color: #333;
}

.book-card p {
  margin: 6px 0;
  font-size: 0.95rem;
  color: #555;
}
