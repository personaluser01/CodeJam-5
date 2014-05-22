﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Reflection;
using System.Xml;
using System.Globalization;

namespace Utils.geom
{
    public static class GeomXmlExt
    {
        public static string ToStringInvariant(this double number)
        {
            return number.ToString(CultureInfo.InvariantCulture);
        }
        public static void WriteAttributeDouble(this System.Xml.XmlWriter writer, string attributeName, double value)
        {
            writer.WriteAttributeString(attributeName, value.ToStringInvariant());
        }
    }

    enum FigureType
    {
        FreePoint,
        Polygon,
        LineTwoPoints,
        Segment
    }

    class Figure
    {
        public string Name;
        public FigureType Type;
        public List<string> deps = new List<string>();
        public string Style;

    }
    public class Drawing
    {
        public double MinimalVisibleX { get; set; }
        public double MinimalVisibleY { get; set; }
        public double MaximalVisibleX { get; set; }
        public double MaximalVisibleY { get; set; }


        internal List<Figure> figures;

        //Style name to color
        internal Dictionary<string,string> shapeColors;
        internal Dictionary<string, string> lineColors;
        internal Dictionary<string, string> pointColors;

        internal Dictionary<Point<long>, string> pointNames;

        const string POINT_NAME_PREFIX = "PointName";
        const string FIGURE_NAME_PREFIX = "FigureName";
        public const long roundingFactor = 1000;

        const string DEFAULT_SHAPE_COLOR = "F5A79E";
        const string DEFAULT_LINE_COLOR = "000000";

        internal const string POINT_STYLE_NAME = "ps";
        internal const string POLYGON_STYLE_NAME = "poly";
        internal const string LINE_STYLE_NAME = "lineS";

        public Drawing() {
            Polygons = new List<List<Point<double>>>();
            pointNames = new Dictionary<Point<long>, string>();
            figures = new List<Figure>();
            shapeColors = new Dictionary<string, string>();
            lineColors = new Dictionary<string, string>();

            MaximalVisibleX = 55;
            MinimalVisibleX = -5;
            MaximalVisibleY = 55;
            MinimalVisibleY = -5;
        }

        public void AddPolygon(IEnumerable<Point<int>> polygon, string color = null)
        {
            color = color ?? DEFAULT_SHAPE_COLOR;

            shapeColors[POLYGON_STYLE_NAME + shapeColors.Count] = color;

            AddPolygon(polygon, POLYGON_STYLE_NAME + (shapeColors.Count - 1), getName);
        }

        public void AddPolygon<T>(IEnumerable<Point<T>> polygon, string styleName, Func<Point<T>, string> getName) where T : IComparable<T>
        {
            Figure f = new Figure();
            f.Name = FIGURE_NAME_PREFIX + figures.Count;
            f.Style = styleName;
            f.Type = FigureType.Polygon;

            foreach(Point<T> p in polygon)
                f.deps.Add(getName(p));
            
            figures.Add(f);
        }

        public void AddAsLine(LineSegment<int> line)
        {
            Figure f = new Figure();
            f.Name = FIGURE_NAME_PREFIX + figures.Count;
            f.Type = FigureType.LineTwoPoints;

            f.deps.Add(getName(line.p1));
            f.deps.Add(getName(line.p2));

            figures.Add(f);
        }

        public void AddAsSeg(LineSegment<int> line)
        {
            AddAsSegMain(line.p1, line.p2, getName);
        }
        public void AddAsSeg(LineSegment<double> line)
        {
            AddAsSegMain(line.p1, line.p2, getName);
        }
        public void AddAsSeg(Point<int> p1, Point<int> p2, string color = null)
        {
            AddAsSegMain(p1, p2, getName, color);
        }

        public void AddAsSegMain<T>(Point<T> p1, Point<T> p2, Func<Point<T>, string> getName, string color = null) where T : IComparable<T>
        {
            color = color ?? DEFAULT_SHAPE_COLOR;

            if (!lineColors.ContainsKey(color))
                lineColors[color] = LINE_STYLE_NAME + lineColors.Count;
            
            

            Figure f = new Figure();
            f.Name = FIGURE_NAME_PREFIX + figures.Count;
            f.Type = FigureType.Segment;
            f.Style = lineColors[color];

            f.deps.Add(getName(p1));
            f.deps.Add(getName(p2));

            figures.Add(f);
        }

        public void AddPoint(Point<int> p, string color = null)
        {
        	 getName(p, color);            
        }

        private string getNameBase(Point<long> point, string color = null)
        {
            if (pointNames.ContainsKey(point))
            {
                return pointNames[point];
            }

            string name = "PointName" + pointNames.Count;
            pointNames[point] = name;
            
            color = color ?? DEFAULT_POINT_COLOR;
        	
        	shapeColors[POINT_STYLE_NAME + shapeColors.Count] = color;
        	
        	Figure f = new Figure();
            f.Name = name;
            f.Style = styleName;
            f.Type = FigureType.FreePoint;
        	
            figures.Add(f);
            
            return name;
        }

        
        private string getName(Point<double> point)
        {
            return getNameBase(new Point<long>( (long) (point.X * roundingFactor), (long)(point.Y * roundingFactor)));
        }
        private string getName(Point<int> point)
        {
            return getNameBase(new Point<long>(point.X * roundingFactor, point.Y * roundingFactor));
        }

        public List<List<Point<double>>> Polygons { get; set; }

    }
    public class GeomXmlWriter
    {
    

        public static void Save(Drawing drawing, string fileName)
        {
            using (var w = XmlWriter.Create(fileName, XmlSettings))
            {
                SaveDrawing(drawing, w);
            }

            //string serialized = SaveDrawing(drawing);
            //File.WriteAllText(fileName, serialized);
        }

        public static string SaveDrawing(Drawing drawing)
        {
            return WriteUsingXmlWriter(w => SaveDrawing(drawing, w));
        }

        public static void SaveDrawing(Drawing drawing, XmlWriter writer)
        {
            GeomXmlWriter serializer = new GeomXmlWriter();
            serializer.Write(drawing, writer);
        }

        public static void SaveDrawing(Drawing drawing, Stream stream)
        {
            using (var writer = XmlWriter.Create(stream, XmlSettings))
            {
                SaveDrawing(drawing, writer);
            }
        }

       

        static XmlWriterSettings XmlSettings
        {
            get
            {
                return new XmlWriterSettings()
                {
                    Indent = true,
                    Encoding = Encoding.UTF8,
                    CloseOutput = true
                };
            }
        }

        public static string WriteUsingXmlWriter(Action<XmlWriter> writerConsumer)
        {
            var sb = new StringBuilder();
            using (var w = XmlWriter.Create(sb, XmlSettings))
            {
                writerConsumer(w);
            }

            return sb.ToString();
        }

        void Write(Drawing drawing, XmlWriter writer)
        {
           
            writer.WriteStartDocument();
            writer.WriteStartElement("Drawing");
           // writer.WriteAttributeDouble("Version", drawing.Version);
          //  writer.WriteAttributeString("Creator", System.Windows.Application.Current.ToString());
            WriteCoordinateSystem(drawing, writer);
            WriteStyles(drawing, writer);
            WriteFigureList(drawing, writer);
            writer.WriteEndElement();
            writer.WriteEndDocument();
        }

        void WriteCoordinateSystem(Drawing drawing, XmlWriter writer)
        {
            writer.WriteStartElement("Viewport");
            writer.WriteAttributeDouble("Left", drawing.MinimalVisibleX);
            writer.WriteAttributeDouble("Top", drawing.MaximalVisibleY);
            writer.WriteAttributeDouble("Right", drawing.MaximalVisibleX);
            writer.WriteAttributeDouble("Bottom", drawing.MinimalVisibleY);

            /*
            var backgroundBrush = drawing.Canvas.Background as SolidColorBrush;
            if (backgroundBrush != null && backgroundBrush.Color != Colors.White)
            {
                writer.WriteAttributeString("Color", backgroundBrush.Color.ToString());
            }

            if (drawing.CoordinateGrid.Locked)
            {
                writer.WriteAttributeBool("Locked", true);
            }
            
            if (drawing.CoordinateGrid.Visible)
            {
                writer.WriteAttributeBool("Grid", true);
                writer.WriteAttributeBool("Axes", drawing.CoordinateGrid.ShowAxes);
            }*/

            writer.WriteEndElement();
        }


        public virtual void WriteStyles(Drawing drawing, XmlWriter writer)
        {
            //<PointStyle Size="10" Fill="" ="true" Color="#" ="1" Name="1" />
            writer.WriteStartElement("Styles");

            writer.WriteStartElement("PointStyle");
            writer.WriteAttributeString("Size", "7");
            writer.WriteAttributeString("Fill", "#FFF8EABA");
            writer.WriteAttributeString("IsFilled", "True");
            writer.WriteAttributeString("Color", "#FF000000");
            writer.WriteAttributeString("StrokeWidth", "5");
            writer.WriteAttributeString("Name", Drawing.POINT_STYLE_NAME);
            writer.WriteEndElement();

            foreach(var style in drawing.shapeColors)
            {
                writer.WriteStartElement("ShapeStyle");
                writer.WriteAttributeString("Size", "10");
                writer.WriteAttributeString("Fill", "#FF" + style.Value);
                writer.WriteAttributeString("IsFilled", "True");
                writer.WriteAttributeString("Color", "#FF000000");
                writer.WriteAttributeString("StrokeWidth", "1");
                writer.WriteAttributeString("Name", style.Key);
                writer.WriteEndElement();
            }
            //<ShapeStyle Fill="" IsFilled="true" Color="" StrokeWidth="1" Name="5" />
            //Color is opagque r g b
            

           foreach(var style in drawing.lineColors)
           {
               writer.WriteStartElement("LineStyle");
               writer.WriteAttributeString("StrokeWidth", "1");
               writer.WriteAttributeString("Color", "#FF" + style.Key);
               writer.WriteAttributeString("Name", style.Value);
               writer.WriteEndElement();

           }

            

            //<LineStyle Color="#FF000000" StrokeWidth="1" Name="4" />
            

            writer.WriteEndElement();
        }

        

       

        public void WriteFigureList(Drawing drawing, XmlWriter writer)
        {
            writer.WriteStartElement("Figures");

            foreach(var pn in drawing.pointNames)
            {
                writer.WriteStartElement("FreePoint");
                writer.WriteAttributeString("Name", pn.Value);
                writer.WriteAttributeString("Style", Drawing.POINT_STYLE_NAME);
                writer.WriteAttributeDouble("X", ( (double)pn.Key.X ) / Drawing.roundingFactor);
                writer.WriteAttributeDouble("Y", ((double)pn.Key.Y) / Drawing.roundingFactor);
                writer.WriteEndElement();
            }

            foreach(var fig in drawing.figures)
            {
                writer.WriteStartElement(fig.Type.ToString());
                writer.WriteAttributeString("Name", fig.Name);
                if (fig.Style != null)
                writer.WriteAttributeString("Style", fig.Style);

                foreach (string dep in fig.deps)
                {
                    writer.WriteStartElement("Dependency");
                    writer.WriteAttributeString("Name", dep);
                    writer.WriteEndElement();
                }
                writer.WriteEndElement();
            }

            /*int pointName = 0;
            int polyName = 0;
            foreach( List<Point<double>> polygon in drawing.Polygons)
            {

                int startPointName = pointName;
                foreach( Point<double> point in polygon)
                {
                    //<FreePoint Name="A" Style="1" X="1600" Y="2700" />
                    writer.WriteStartElement("FreePoint");
                    writer.WriteAttributeString("Name", "Point" + pointName++);
                    writer.WriteAttributeString("Style", POINT_STYLE_NAME);
                    writer.WriteAttributeDouble("X", point.X);
                    writer.WriteAttributeDouble("Y", point.Y);
                    writer.WriteEndElement();
                }
                int endPointName = pointName;

                writer.WriteStartElement("Polygon");
                writer.WriteAttributeString("Name", "PolyName" + polyName);
                writer.WriteAttributeString("Style", POLYGON_STYLE_NAME);

                for (int pn = startPointName; pn < endPointName; ++pn )
                {
                    writer.WriteStartElement("Dependency");
                    writer.WriteAttributeString("Name", "Point" + pn);
                    writer.WriteEndElement();
                }
                writer.WriteEndElement();

                    
            }*/
            
            writer.WriteEndElement();
        }


       
    }
}


