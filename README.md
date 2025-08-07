import React, { useMemo } from "react";
import {
  useTable,
  useSortBy,
  useFilters,
  useColumnOrder,
  useFlexLayout,
  useBlockLayout,
  useColumnVisibility,
} from "react-table";
import { CSVLink } from "react-csv";

// Sample filtering component
const SelectColumnFilter = ({ column: { filterValue, setFilter, preFilteredRows, id } }) => {
  const options = useMemo(() => {
    const opts = new Set();
    preFilteredRows.forEach(row => {
      opts.add(row.values[id]);
    });
    return [...opts.values()];
  }, [id, preFilteredRows]);

  return (
    <select
      value={filterValue || ""}
      onChange={e => setFilter(e.target.value || undefined)}
      className="border rounded px-2 py-1"
    >
      <option value="">All</option>
      {options.map((option, i) => (
        <option key={i} value={option}>
          {option}
        </option>
      ))}
    </select>
  );
};

const BondTable = ({ data }) => {
  const columns = useMemo(
    () => [
      {
        Header: "ISIN",
        accessor: "security.isin",
      },
      {
        Header: "Issuer",
        accessor: "security.issuerName",
      },
      {
        Header: "Maturity Date",
        accessor: "security.maturityDate",
      },
      {
        Header: "Status",
        accessor: "security.status",
        Filter: SelectColumnFilter,
        filter: "includes",
      },
      {
        Header: "Assignment",
        accessor: row => (row.book ? "Assigned" : "Unassigned"),
        id: "assignment",
      },
      {
        Header: "Book Name",
        accessor: row => row.book?.name || "—",
        id: "bookName",
      },
      {
        Header: "Trade Date",
        accessor: "tradeDate",
      },
      {
        Header: "Counterparty",
        accessor: "counterparty.name",
      },
    ],
    []
  );

  const tableInstance = useTable(
    {
      columns,
      data,
      defaultColumn: { Filter: () => null },
    },
    useFilters,
    useSortBy,
    useColumnOrder,
    useColumnVisibility,
    useFlexLayout
  );

  const {
    getTableProps,
    getTableBodyProps,
    headerGroups,
    rows,
    prepareRow,
    allColumns,
    setColumnOrder,
    state,
  } = tableInstance;

  const exportHeaders = columns.map(col => ({
    label: col.Header,
    key: typeof col.accessor === "string" ? col.accessor : col.id,
  }));

  const csvData = rows.map(row => {
    const flatRow = {};
    columns.forEach(col => {
      const val = typeof col.accessor === "string"
        ? col.accessor.split('.').reduce((o, k) => o?.[k], row.original)
        : col.accessor(row.original);
      flatRow[col.Header] = val;
    });
    return flatRow;
  });

  return (
    <div className="p-4">
      <div className="mb-4 flex flex-wrap gap-4 items-center">
        <div>
          <strong>Toggle Columns:</strong>
          <div className="flex flex-wrap gap-2 mt-2">
            {allColumns.map(column => (
              <label key={column.id} className="flex items-center gap-1">
                <input type="checkbox" {...column.getToggleHiddenProps()} />
                {column.Header}
              </label>
            ))}
          </div>
        </div>

        <CSVLink data={csvData} headers={exportHeaders} filename="bonds.csv">
          <button className="bg-blue-600 text-white px-3 py-1 rounded">Export CSV</button>
        </CSVLink>
      </div>

      <div className="overflow-x-auto">
        <table {...getTableProps()} className="w-full border border-gray-300">
          <thead className="bg-gray-100">
            {headerGroups.map(headerGroup => (
              <tr {...headerGroup.getHeaderGroupProps()}>
                {headerGroup.headers.map(column => (
                  <th
                    {...column.getHeaderProps(column.getSortByToggleProps())}
                    className="px-4 py-2 border"
                  >
                    {column.render("Header")}
                    {column.isSorted && (column.isSortedDesc ? " 🔽" : " 🔼")}
                    <div>{column.canFilter ? column.render("Filter") : null}</div>
                  </th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody {...getTableBodyProps()}>
            {rows.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="text-center py-4">
                  No data found.
                </td>
              </tr>
            ) : (
              rows.map(row => {
                prepareRow(row);
                return (
                  <tr {...row.getRowProps()} className="border-t">
                    {row.cells.map(cell => (
                      <td {...cell.getCellProps()} className="px-4 py-2 border">
                        {cell.render("Cell")}
                      </td>
                    ))}
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default BondTable;
