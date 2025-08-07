import React from 'react';
import './BondAnalytics.css';

const BondAnalytics = ({ bonds = [] }) => {
  // Generate chart data from bonds
  const generateMaturityChart = () => {
    if (!bonds || bonds.length === 0) {
      return Array.from({ length: 7 }, (_, i) => ({
        day: ['S', 'M', 'T', 'W', 'T', 'F', 'S'][new Date().getDay()],
        value: 0,
        height: '10%',
        date: new Date().toDateString()
      }));
    }

    const days = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
    const today = new Date();
    const chartData = [];

    for (let i = 0; i < 7; i++) {
      const date = new Date(today);
      date.setDate(today.getDate() + i);
      
      // Count bonds maturing on this date
      const maturingBonds = bonds.filter(bond => {
        if (bond.maturityDate === "N/A" || !bond.maturityDate) return false;
        try {
          const maturityDate = new Date(bond.maturityDate);
          return maturityDate.toDateString() === date.toDateString();
        } catch {
          return false;
        }
      });

      const count = maturingBonds.length;
      
      chartData.push({
        day: days[date.getDay()],
        value: count,
        date: date.toDateString()
      });
    }

    // Calculate heights based on max value
    const maxValue = Math.max(...chartData.map(d => d.value), 1);
    chartData.forEach(data => {
      data.height = data.value === 0 ? '10%' : `${Math.max(15, (data.value / maxValue) * 85)}%`;
    });

    return chartData;
  };

  // Calculate summary statistics
  const calculateStats = () => {
    if (!bonds || bonds.length === 0) {
      return {
        totalBonds: 0,
        maturingThisWeek: 0,
        averageCoupon: 0,
        totalValue: 0
      };
    }

    const today = new Date();
    const weekFromNow = new Date(today);
    weekFromNow.setDate(today.getDate() + 7);

    const maturingThisWeek = bonds.filter(bond => {
      if (bond.maturityDate === "N/A" || !bond.maturityDate) return false;
      try {
        const maturityDate = new Date(bond.maturityDate);
        return maturityDate >= today && maturityDate <= weekFromNow;
      } catch {
        return false;
      }
    }).length;

    const validCoupons = bonds
      .map(bond => parseFloat(bond.coupon))
      .filter(coupon => !isNaN(coupon) && coupon > 0);
    
    const averageCoupon = validCoupons.length > 0 
      ? (validCoupons.reduce((sum, coupon) => sum + coupon, 0) / validCoupons.length).toFixed(1)
      : 0;

    const totalValue = bonds
      .reduce((sum, bond) => {
        const price = parseFloat(bond.price) || 0;
        const quantity = parseFloat(bond.quantity) || 0;
        return sum + (price * quantity);
      }, 0);

    return {
      totalBonds: bonds.length,
      maturingThisWeek,
      averageCoupon: `${averageCoupon}%`,
      totalValue: `$${(totalValue / 1000000).toFixed(1)}M`
    };
  };

  const chartData = generateMaturityChart();
  const stats = calculateStats();

  return (
    <div className="bond-analytics">
      <h2 className="analytics-title">Bond Analytics - Maturity Timeline</h2>
      
      {bonds.length === 0 ? (
        <div className="no-data">No bond data available</div>
      ) : (
        <>
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
          
          <div className="analytics-summary">
            <div className="summary-item">
              <span className="summary-label">Total Bonds</span>
              <span className="summary-value">{stats.totalBonds}</span>
            </div>
            <div className="summary-item">
              <span className="summary-label">Maturing This Week</span>
              <span className="summary-value">{stats.maturingThisWeek}</span>
            </div>
            <div className="summary-item">
              <span className="summary-label">Average Coupon</span>
              <span className="summary-value">{stats.averageCoupon}</span>
            </div>
            <div className="summary-item">
              <span className="summary-label">Total Value</span>
              <span className="summary-value">{stats.totalValue}</span>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default BondAnalytics;


---

.bond-analytics {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 16px;
            padding: 24px;
            color: white;
            box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
            margin: 20px;
            min-height: 300px;
        }

        .analytics-title {
            font-size: 1.5rem;
            font-weight: 700;
            margin: 0 0 24px 0;
            text-align: center;
            background: linear-gradient(45deg, #fff, #e0e7ff);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .chart-container {
            position: relative;
            height: 200px;
            margin-bottom: 24px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            padding: 20px;
            backdrop-filter: blur(10px);
        }

        .chart-data {
            display: flex;
            justify-content: space-around;
            align-items: flex-end;
            height: 100%;
            position: relative;
            z-index: 2;
        }

        .chart-bar {
            display: flex;
            flex-direction: column;
            align-items: center;
            flex: 1;
            max-width: 60px;
        }

        .bar {
            width: 24px;
            background: linear-gradient(180deg, #4ade80 0%, #16a34a 100%);
            border-radius: 4px 4px 0 0;
            margin-bottom: 8px;
            min-height: 8px;
            transition: all 0.3s ease;
            cursor: pointer;
            box-shadow: 0 2px 8px rgba(74, 222, 128, 0.3);
        }

        .bar:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 16px rgba(74, 222, 128, 0.5);
        }

        .bar-label {
            font-size: 0.75rem;
            font-weight: 600;
            color: rgba(255, 255, 255, 0.8);
        }

        .chart-pattern {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            z-index: 1;
            opacity: 0.1;
        }

        .pattern-lines {
            height: 100%;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            padding: 20px;
        }

        .pattern-line {
            height: 1px;
            background: rgba(255, 255, 255, 0.2);
            width: 100%;
        }

        .analytics-summary {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 16px;
            margin-top: 20px;
        }

        .summary-item {
            background: rgba(255, 255, 255, 0.15);
            border-radius: 12px;
            padding: 16px;
            text-align: center;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            transition: transform 0.3s ease;
        }

        .summary-item:hover {
            transform: translateY(-2px);
            background: rgba(255, 255, 255, 0.2);
        }

        .summary-label {
            display: block;
            font-size: 0.875rem;
            color: rgba(255, 255, 255, 0.8);
            margin-bottom: 8px;
            font-weight: 500;
        }

        .summary-value {
            display: block;
            font-size: 1.5rem;
            font-weight: 700;
            color: #fff;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
        }

        .no-data {
            text-align: center;
            color: rgba(255, 255, 255, 0.7);
            font-style: italic;
            margin: 40px 0;
        }

        /* Responsive design */
        @media (max-width: 768px) {
            .bond-analytics {
                margin: 10px;
                padding: 16px;
            }
            
            .analytics-title {
                font-size: 1.25rem;
            }
            
            .chart-container {
                height: 160px;
                padding: 16px;
            }
            
            .bar {
                width: 20px;
            }
            
            .analytics-summary {
                grid-template-columns: 1fr 1fr;
                gap: 12px;
            }
            
            .summary-item {
                padding: 12px;
            }
        }
