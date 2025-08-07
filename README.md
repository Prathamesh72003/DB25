import React, { useEffect, useState } from 'react';
import './styles/myBonds.css';
import DetailsPopup from './DetailsPopup';
import FilterButton from './FilterButton';

const MyBondsV2 = ({ data }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedBondId, setSelectedBondId] = useState(null);
  const [searchCriteria, setSearchCriteria] = useState('isin');
  const [searchTerm, setSearchTerm] = useState('');
  const [visibleColumns, setVisibleColumns] = useState(['isin', 'issuer', 'maturity', 'daystomaturity', 'status']);

  const [bonds, setBonds] = useState([]);

  const allColumns = [
    { key: 'isin', label: 'ISIN' },
    { key: 'issuer', label: 'Issuer' },
    { key: 'maturity', label: 'Maturity Date' },
    { key: 'daystomaturity', label: 'Assignment' },
    { key: 'status', label: 'Status' }
  ];

  useEffect(() => {
    if (data?.length) {
      const processed = data.map(bond => ({
        isin: bond.security?.isin,
        issuer: bond.issuer?.userName,
        maturity: bond.security?.maturityDate,
        daystomaturity: bond.book ? 'Assigned' : 'Unassigned',
        status: bond.security?.status
      }));
      setBonds(processed);
    }
  }, [data]);

  const filteredBonds = bonds.filter(bond =>
    bond[searchCriteria]?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const toggleColumn = key => {
    setVisibleColumns(prev =>
      prev.includes(key) ? prev.filter(col => col !== key) : [...prev, key]
    );
  };

  const handleExportCSV = () => {
    const headers = allColumns
      .filter(col => visibleColumns.includes(col.key))
      .map(col => col.label);

    const rows = filteredBonds.map(bond =>
      visibleColumns.map(key => bond[key])
    );

    const csvContent =
      'data:text/csv;charset=utf-8,' +
      [headers.join(','), ...rows.map(r => r.join(','))].join('\n');

    const link = document.createElement('a');
    link.href = encodeURI(csvContent);
    link.download = 'my_bonds.csv';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleRowClick = isin => {
    setSelectedBondId(isin);
    setIsOpen(true);
  };

  return (
    <>
      {isOpen && (
        <DetailsPopup
          selectedBondId={selectedBondId}
          isOpen={isOpen}
          setIsOpen={setIsOpen}
        />
      )}

      <div className="mybonds-wrapper">
        <h2>My Bonds</h2>

        <div className="toolbar">
          <div className="search-group">
            <select
              className="criteria"
              value={searchCriteria}
              onChange={e => setSearchCriteria(e.target.value)}
            >
              <option value="isin">ISIN</option>
              <option value="issuer">Issuer</option>
              <option value="status">Status</option>
            </select>
            <input
              type="text"
              placeholder={`Search by ${searchCriteria}`}
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="actions">
            <FilterButton />
            <button onClick={handleExportCSV}>Export CSV</button>
          </div>
        </div>

        <div className="column-toggle">
          <h4>Visible Columns</h4>
          {allColumns.map(col => (
            <label key={col.key}>
              <input
                type="checkbox"
                checked={visibleColumns.includes(col.key)}
                onChange={() => toggleColumn(col.key)}
              />
              {col.label}
            </label>
          ))}
        </div>

        <table className="bonds-table">
          <thead>
            <tr>
              {allColumns
                .filter(col => visibleColumns.includes(col.key))
                .map(col => (
                  <th key={col.key}>{col.label}</th>
                ))}
            </tr>
          </thead>
          <tbody>
            {filteredBonds.map(bond => (
              <tr key={bond.isin} onClick={() => handleRowClick(bond.isin)}>
                {visibleColumns.includes('isin') && <td>{bond.isin}</td>}
                {visibleColumns.includes('issuer') && <td>{bond.issuer}</td>}
                {visibleColumns.includes('maturity') && <td>{bond.maturity}</td>}
                {visibleColumns.includes('daystomaturity') && (
                  <td>{bond.daystomaturity}</td>
                )}
                {visibleColumns.includes('status') && (
                  <td className={`status ${bond.status.toLowerCase()}`}>
                    {bond.status}
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
};

---

.mybonds-wrapper {
  max-width: 1200px;
  margin: auto;
  padding: 2rem;
  font-family: Arial, sans-serif;
}

h2 {
  margin-bottom: 1rem;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.search-group {
  display: flex;
  gap: 0.5rem;
}

.criteria {
  padding: 0.5rem;
}

.search-group input {
  padding: 0.5rem;
  width: 200px;
}

.actions {
  display: flex;
  gap: 1rem;
}

.actions button {
  padding: 0.5rem 1rem;
  cursor: pointer;
}

.column-toggle {
  margin-bottom: 1rem;
}

.column-toggle label {
  margin-right: 1rem;
  display: inline-block;
}

.bonds-table {
  width: 100%;
  border-collapse: collapse;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
}

.bonds-table th,
.bonds-table td {
  padding: 0.75rem;
  text-align: left;
  border: 1px solid #ccc;
}

.bonds-table tbody tr:hover {
  background-color: #f9f9f9;
  cursor: pointer;
}

.status.active {
  color: green;
  font-weight: bold;
}

.status.closed {
  color: red;
  font-weight: bold;
}


export default MyBondsV2;
