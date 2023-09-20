package math;

import java.util.ArrayList;
import java.util.List;

/**
 * K-Means clustering
 *
 * @author ZHANG Baizhou zhangbz
 * @project city_site_matching
 * @date 2021/11/10
 * @time 16:31
 */
public class ZKMeans {
    private final Sample[] originalSamples;
    private final int k;
    private final double epsilon;

    private final double[][] centroid;
    private final int[][] clusters;

    /* ------------- constructor ------------- */

    public ZKMeans(double[][] samples, int _k, double _epsilon) {
        this.originalSamples = new Sample[samples.length];
        for (int i = 0; i < samples.length; i++) {
            originalSamples[i] = new Sample(samples[i], i);
        }
        this.k = _k;
        this.epsilon = _epsilon;

        this.centroid = initCentroid();
        this.clusters = new int[k][];
    }

    /* ------------- member function ------------- */

    /**
     * perform K-means clustering
     */
    public void cluster() {
        // iterate until the maximum moving distance of centroids is less than the epsilon
        double maxCentroidMoveDist = Double.MAX_VALUE;
        List<List<Sample>> currClusters = new ArrayList<>();
        while (maxCentroidMoveDist > epsilon) {
            // assign each existing data point to its nearest centroid
            currClusters = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                currClusters.add(new ArrayList<>());
            }
            for (Sample s : originalSamples) {
                double[] dists = new double[k];
                for (int j = 0; j < k; j++) {
                    double[] c = centroid[j];
                    double d = ZMath.distanceEuclidean(c, s.data);
                    dists[j] = d;
                }
                int min = ZMath.getMinIndex(dists);
                s.groupID = min;
                currClusters.get(min).add(s);
            }
            // move the centroids to the average location of points assigned to it
            double maxMoveDists = -1;
            for (int i = 0; i < k; i++) {
                if (!currClusters.get(i).isEmpty()) {
                    // current cluster has elements
                    double[][] groupData = new double[currClusters.get(i).size()][];
                    for (int j = 0; j < groupData.length; j++) {
                        groupData[j] = currClusters.get(i).get(j).data;
                    }
                    double[] newCentroid = ZMath.dataCentroid(groupData);
                    double moveD = ZMath.distanceEuclidean(centroid[i], newCentroid);
                    if (moveD > maxMoveDists) {
                        maxMoveDists = moveD;
                    }
                    centroid[i] = newCentroid;
                } else {
                    // current cluster is empty
                    centroid[i] = new double[]{};
                }
            }
            maxCentroidMoveDist = maxMoveDists;
        }

        // assign original IDs to final cluster results
        for (int i = 0; i < k; i++) {
            clusters[i] = new int[currClusters.get(i).size()];
            for (int j = 0; j < currClusters.get(i).size(); j++) {
                clusters[i][j] = currClusters.get(i).get(j).id;
            }
        }
    }

    /**
     * initialize the centroids using K-means++ method
     *
     * @return double[][]
     */
    private double[][] initCentroid() {
        // randomly add first centroid
        List<double[]> cen = new ArrayList<>();
        int random = ZMath.randomInteger(0, originalSamples.length);
        cen.add(originalSamples[random].data);

        // iterate all samples for k-1 times
        // each time to find the farthest sample to the existing centroids
        // then add the farthest sample to the initial centroid
        for (int i = 1; i < k; i++) {
            double[] minDists = new double[originalSamples.length];
            for (int j = 0; j < originalSamples.length; j++) {
                Sample s = originalSamples[j];
                double minDistForSample = Double.MAX_VALUE;
                for (double[] currCentroids : cen) {
                    double dist = ZMath.distanceEuclidean(s.data, currCentroids);
                    if (dist < minDistForSample) {
                        minDistForSample = dist;
                    }
                }
                minDists[j] = minDistForSample;
            }
            int max = ZMath.getMaxIndex(minDists);
            cen.add(originalSamples[max].data);
        }

        double[][] result = new double[k][];
        for (int i = 0; i < cen.size(); i++) {
            result[i] = cen.get(i);
        }
        return result;
    }

    /* ------------- setter & getter ------------- */

    public double[][] getCentroid() {
        return centroid;
    }

    public int[][] getClusters() {
        return clusters;
    }

    /* ------------- inner class ------------- */

    private static class Sample {
        private final double[] data;
        private final int id;
        private int groupID;

        private Sample(double[] data, int id) {
            this.data = data;
            this.id = id;
            this.groupID = 0;
        }
    }
}
