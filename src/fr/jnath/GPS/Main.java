package fr.jnath.GPS;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Main {
	public Main() {
		// Créer graph
		Graph map = new Graph();
		map.addPoint(new Point("A", -4, 0));
		map.addPoint(new Point("B", 4, 0));
		map.addPoint(new Point("C", 1, -4));
		map.addPoint(new Point("D", -1, -4));
		map.addSegment(new Segment(Point.search("A"), Point.search("B"), 8));
		map.addSegment(new Segment(Point.search("B"), Point.search("C"), 5));
		map.addSegment(new Segment(Point.search("C"), Point.search("D"), 2));
		map.addSegment(new Segment(Point.search("D"), Point.search("A"), 5));
		// InddPoint(stancier GPS
		GPS gps = new GPS(new GraphGPS(map));
		// Recherche chemin A -> B
		//Path path = gps.searchPath("A","B");
		Path chemin1 = gps.search(Point.search("A"), Point.search("D"));
		chemin1.displayPath();
		//display path;
		// A -> C
		Path chemin2 = gps.search(Point.search("A"), Point.search("C"));
		chemin2.displayPath();
		// B -> A
		Path chemin3 = gps.search(Point.search("B"), Point.search("A"));
		chemin3.displayPath();
		// B -> D
		Path chemin4 = gps.search(Point.search("B"), Point.search("D"));
		chemin4.displayPath();
		
	}
	public static void main(String [] args) {
		Main _main = new Main();
	}
}
class Point {
	private int _x;
	private int _y;
	private String _name;
	private static TreeMap<String, Point> s_all_points = new TreeMap<String, Point>();
	private List<Segment> segment_with_me = new ArrayList<Segment>();
	private List<Point> adjasent_point_with_me = new ArrayList<Point>();
	Point(String name, int x, int y){
		_x=x;
		_y=y;
		_name=name;
		s_all_points.put(name,this);
	};

	public String get_name() {
		return _name;
	}
	public int get_x() {
		return _x;
	}
	public int get_y() {
		return _y;
	}
	static Point search(String name) {
		return s_all_points.get(name);
	}
	public void segmentWithMeAdd(Segment seg) {
		segment_with_me.add(seg);
	}
	public void adjasentPointAdd(Point point) {
		adjasent_point_with_me.add(point);
	}
	public List<Segment> segmentWithMe(){
		return segment_with_me;
	}
	public List<Point> adjasentPoint() {
		return adjasent_point_with_me;
	}
}

class Segment {
	private Point _pointA;
	private Point _pointB;
	private double _dist;
	private static List<Segment> s_all_segment = new ArrayList<Segment>();
	public Segment(Point A,Point B, double dist) {
		_pointA=A;
		_pointB=B;

		_dist=dist;
		s_all_segment.add(this);
		_pointA.segmentWithMeAdd(this);
		_pointB.segmentWithMeAdd(this);
		_pointA.adjasentPointAdd(_pointB);
		_pointB.adjasentPointAdd(_pointA);
	}
	public double get_dist() {
		return _dist;
	};
	public Point get_pointA() {
		return _pointA;
	}
	public Point get_pointB() {
		return _pointB;
	}
}


class Graph {
	private List<Point> _PointsInGraph = new ArrayList<Point>();
	private List<Segment> _SegmentsInGraph = new ArrayList<Segment>();
	public void addPoint(Point point){
		_PointsInGraph.add(point);
	}
	
	public void addSegment(Segment seg){
		_SegmentsInGraph.add(seg);
	}
	
	public List<Point> pointGraph(){
		return _PointsInGraph;
	}
	
	public List<Segment> segmentGraph(){
		return _SegmentsInGraph;
	}

}

class PointGPS {
	Point p;
	boolean estDejapasse;
	static TreeMap<String, PointGPS> s_all_point = new TreeMap<String, PointGPS>();
	public PointGPS(Point point) {
		p=point;
		estDejapasse=false;
		s_all_point.put(point.get_name(), this);
	}
	public static PointGPS searchWithPoint(String point) {
		return s_all_point.get(point);
	}
}

class GraphGPS{
	private List<PointGPS> _PointsGPSInGraph = new ArrayList<PointGPS>();
	private List<Point> _PointsInGraph = new ArrayList<Point>();
	private List<Segment> _SegmentsInGraph = new ArrayList<Segment>();
	public GraphGPS(Graph graph) {
		_PointsInGraph=graph.pointGraph();
		_SegmentsInGraph=graph.segmentGraph();
		for(Point pnt : graph.pointGraph()) {
			_PointsGPSInGraph.add(new PointGPS(pnt));
		}
	}

	public List<Point> pointGraph(){
		return _PointsInGraph;
	}

	public List<PointGPS> pointGPSGraph(){
		return _PointsGPSInGraph;
	}
	public List<Segment> segmentGraph(){
		return _SegmentsInGraph;
	}
}
class Path{
	List<PointGPS> _pointPast = new ArrayList<PointGPS>();
	public Path(PointGPS start) {
		_pointPast.add(start);
	}
	public boolean addNextVerif(PointGPS point) {
		if(point.estDejapasse) {
			return false;
		}
		return true;
	}
	public Path addNext(PointGPS point) {
		_pointPast.add(point);
		point.estDejapasse=true;
		return this;
	}
	public PointGPS getLastPoint() {
		return _pointPast.get(_pointPast.size()-1);
	}
	public void displayPath() {
		String display="";
		for (PointGPS point : _pointPast) {
			display+=point.p.get_name()+" ";
		}
		System.out.println(display);
	}
}
class GPS {
	private GraphGPS _map;
	public GPS(GraphGPS map) {
		_map=map;
	}
	public Path search(Point start, Point end) {
		List<Path> _chemins = new ArrayList<Path>();
		List<Path> _newChemins = new ArrayList<Path>();
		_chemins.clear();
		_newChemins.clear();
		if (!(_map.pointGraph().contains(start)&& _map.pointGraph().contains(end))) {
			System.out.println("Un ou les point n'existe pas");
			return null;
		}
		//init
		_chemins.add(new Path(PointGPS.searchWithPoint(start.get_name())));
		while (true) {
			// On recherche les point adjasent du dernier point de chaque chemin on ajoute les nouveau chemin à une liste
			for(Path chemin : _chemins) {
				for (Point pAdja :chemin.getLastPoint().p.adjasentPoint()) {
					Path p=null;
					for (PointGPS i : chemin._pointPast) {
						if (p==null) {
							p=new Path(i);
						}else{
							p.addNext(i);
						}
					}
					if(p.addNextVerif(PointGPS.searchWithPoint(pAdja.get_name()))) {
						p.addNext(PointGPS.searchWithPoint(pAdja.get_name()));
						_newChemins.add(p);
					}
				}
			}
			// On vérifi si on as fini et on return le chemin sinon on reboucle
			for (Path chemin : _newChemins){
				if (chemin.getLastPoint().p==end){
					for(PointGPS p : _map.pointGPSGraph()) {
						p.estDejapasse=false;
					}
					return chemin;
				}
			}
			_chemins.clear();
			for(Path element : _newChemins) {
				_chemins.add(element);
			}
			_newChemins.clear();
		}
	}
}
