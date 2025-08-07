import React from 'react';
import './BondAnalytics.css';

const BondAnalytics = ({ bonds }) => {
  // Generate chart data from bonds
  const generateChartData = () => {
    if (!bonds || bonds.length === 0) {
      return [];
    }

    // Get last 7 days including today
    const days = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
    const today = new Date();
    const chartData = [];

    for (let i = 6; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(today.getDate() - i);
      
      // Count bonds traded on this date
      const bondsOnThisDay = bonds.filter(bond => {
        if (bond.tradeDate === "N/A") return false;
        const tradeDate = new Date(bond.tradeDate);
        return tradeDate.toDateString() === date.toDateString();
      });

      const count = bondsOnThisDay.length;
      const maxCount = Math.max(...bonds.map(() => 1)) * bonds.length * 0.1; // Reasonable max for scaling
      const height = count === 0 ? '5%' : `${Math.min((count / maxCount) * 100, 100)}%`;

      chartData.push({
        day: days[date.getDay()],
        value: count,
        height: height,
        date: date.toDateString()
      });
    }

    return chartData;
  };

  // Alternative: Group by maturity dates approaching (next 7 days)
  const generateMaturityChart = () => {
    if (!bonds || bonds.length === 0) {
      return [];
    }

    const days = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
    const today = new Date();
    const chartData = [];

    for (let i = 0; i < 7; i++) {
      const date = new Date(today);
      date.setDate(today.getDate() + i);
      
      // Count bonds maturing on this date
      const maturingBonds = bonds.filter(bond => {
        if (bond.maturityDate === "N/A") return false;
        const maturityDate = new Date(bond.maturityDate);
        return maturityDate.toDateString() === date.toDateString();
      });

      const count = maturingBonds.length;
      const maxCount = Math.max(1, Math.max(...Array.from({length: 7}, (_, idx) => {
        const checkDate = new Date(today);
        checkDate.setDate(today.getDate() + idx);
        return bonds.filter(bond => {
          if (bond.maturityDate === "N/A") return false;
          const maturityDate = new Date(bond.maturityDate);
          return maturityDate.toDateString() === checkDate.toDateString();
        }).length;
      })));
      
      const height = count === 0 ? '5%' : `${Math.min((count / maxCount) * 80 + 20, 100)}%`;

      chartData.push({
        day: days[date.getDay()],
        value: count,
        height: height,
        date: date.toDateString()
      });
    }

    return chartData;
  };

  // Use maturity-based chart as it's more meaningful for bonds
  const chartData = generateMaturityChart();

  return (
    <div className="bond-analytics">
      <h2 className="analytics-title">Bond Analytics - Maturity Timeline</h2>
      <div className="chart-container">
        <div className="chart-data">
          {chartData.map((data, index) => (
            <div key={index} className="chart-bar">
              <div
                className="bar"
                style={{ height: data.height }}
                title={`${data.day}: ${data.value} bonds maturing`}
              ></div>
              <span className="bar-label">{data.day}</span>
            </div>
          ))}
        </div>
        <div className="chart-pattern">
          <div className="pattern-lines">
            {[...Array(8)].map((_, i) => (
              <div key={i} className="pattern-line"></div>
            ))}
          </div>
        </div>
      </div>
      
      {/* Summary stats */}
      <div className="analytics-summary">
        <div className="summary-item">
          <span className="summary-label">Total Bonds:</span>
          <span className="summary-value">{bonds?.length || 0}</span>
        </div>
        <div className="summary-item">
          <span className="summary-label">Maturing This Week:</span>
          <span className="summary-value">
            {chartData.reduce((sum, day) => sum + day.value, 0)}
          </span>
        </div>
      </div>
    </div>
  );
};

export default BondAnalytics;



---

.analytics-summary {
  display: flex;
  gap: 20px;
  margin-top: 15px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.summary-label {
  font-size: 0.8em;
  color: #666;
}

.summary-value {
  font-weight: bold;
  font-size: 1.1em;
}
