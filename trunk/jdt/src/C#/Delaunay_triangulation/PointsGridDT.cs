/*************************************************
 * Written by:  Tal Shargal
 * Date:        25/12/09
 *************************************************/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using C5;

namespace JDT_NET.Delaunay_triangulation
{
    public class PointsGridDT
    {
        private Delaunay_Triangulation _dt;
        private Dictionary<Point_dt,Triangle_dt> _points2Triangles;
        private Dictionary<Triangle_dt, Point_dt> _triangles2Points;
        private const int DefaultMatrixSize = 5;
        private readonly int _matrixSize;

        private bool _preCalculated = false;
        private decimal _xInterval;
        private decimal _yInterval;

        public Delaunay_Triangulation DelaunayTriangulation
        {
            get { return _dt;}
            set { _dt = value;}
        }

        public PointsGridDT() : this(DefaultMatrixSize)
        {            
        }

        /// <summary>
        /// Constructor of Points grid. Doesn't call Precalculate!
        /// </summary>
        /// <param name="matrixSize">at least 2</param>
        public PointsGridDT(int matrixSize)
        {
            if (matrixSize<2)
            {
                throw new ArgumentException("matrixSize must be greater than 1");
            }
            _matrixSize = matrixSize;
            _points2Triangles = new Dictionary<Point_dt, Triangle_dt>(_matrixSize * _matrixSize);
            _triangles2Points = new Dictionary<Triangle_dt, Point_dt>(_matrixSize * _matrixSize);
            _dt = null;
        }

        /// <summary>
        /// Constructor of Points grid. Doesn't call Precalculate!
        /// </summary>
        /// <param name="matrixSize">at least 2</param>
        /// <param name="delaunayTriangulation">triangulation to work on</param>
        public PointsGridDT(int matrixSize, Delaunay_Triangulation delaunayTriangulation)
        {
            _matrixSize = matrixSize;
            _points2Triangles = new Dictionary<Point_dt, Triangle_dt>(_matrixSize * _matrixSize);
            _triangles2Points = new Dictionary<Triangle_dt, Point_dt>(_matrixSize * _matrixSize);
            _dt = delaunayTriangulation;
        }

        private void PreCalculate()
        {
            if (_dt == null)
            {
                throw new InvalidOperationException("Delaunay_Triangulation must be set before calling this method");
            }

            _points2Triangles.Clear();
            _triangles2Points.Clear();
            
            Point_dt maxPoint = _dt.PotentialBbMax;

            _xInterval = (decimal)maxPoint.x /(_matrixSize - 1);
            _yInterval = (decimal)maxPoint.y /(_matrixSize - 1);

            for (decimal xAxis = 0; xAxis <= (int)Math.Floor(maxPoint.x); xAxis += _xInterval)            
            {
                for (decimal yAxis = 0; yAxis <= (int)Math.Floor(maxPoint.y); yAxis += _yInterval)
                {
                    var anchorPoint = new Point_dt((double)xAxis, (double)yAxis);
                    var correspondTriangle = _dt.find(anchorPoint);
                    _points2Triangles[anchorPoint] = correspondTriangle;
                    _triangles2Points[correspondTriangle] = anchorPoint;
                }
            }

            _preCalculated = true;
        }

        public Triangle_dt FindClosestTriangle(Point_dt p)
        {
            if (!_preCalculated) // if no grid was build (because of no necessary)
            {
                if (_dt == null)
                {
                    throw new InvalidOperationException("Delaunay_Triangulation must be set before calling this method");
                }
                return _dt.startTriangleHull;
            }

            return _points2Triangles[FindClosestPoint(p)];
        }

        private Point_dt FindClosestPoint(Point_dt p)
        {            
            var basePointX = Math.Floor((decimal)p.x / _xInterval) * _xInterval;
            var basePointY = Math.Floor((decimal)p.y / _yInterval) * _yInterval;
            var basePoint = new Point_dt((double) basePointX, (double) basePointY);
            return basePoint;
        }

        public void UpdateTriangleFlip(Triangle_dt oldT, Triangle_dt newT1, Triangle_dt newT2)
        {
            if (!_preCalculated)
            {
                PreCalculate();
            }

            if (_triangles2Points.ContainsKey(oldT))
            {
                var anchorPoint = _triangles2Points[oldT];
                if (newT1.contains(anchorPoint))
                {
                    _points2Triangles[anchorPoint] = newT1;
                    _triangles2Points.Remove(oldT);
                    _triangles2Points[newT1] = anchorPoint;
                }
                else if (newT2.contains(anchorPoint))
                {
                    _points2Triangles[anchorPoint] = newT2;
                    _triangles2Points.Remove(oldT);
                    _triangles2Points[newT2] = anchorPoint;
                }
                else
                {
                    throw new Exception("Internal error occured");
                }
            }            
        }
    }
}

