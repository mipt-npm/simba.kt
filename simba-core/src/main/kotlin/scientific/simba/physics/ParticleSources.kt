package scientific.simba.physics

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import scientifik.kmath.chains.Chain
import scientifik.simulation.simba.core.PrimaryGenerator

abstract class ParticleSource<P: Particle> : Chain<P>{

    val asPrimaryGenerator = object : PrimaryGenerator<P>{
        override fun fork(): Chain<Flow<P>> {
            return this
        }

        override suspend fun next(): Flow<P> {
            return flow {
                emit(this@ParticleSource.next())
            }
        }
    }


}

class ParticleGun: ParticleSource<HEPParticle>(){

    var definition: ParticleDefinition = NeutralFake
    var kineticEnergy: Double = 1.0
    var momentumDirection: Vector3D = Vector3D(0.0, 0.0, -1.0)
    var momentum: Vector3D =  Vector3D(0.0, 0.0, -1.0)
    var position : Vector3D =  Vector3D(0.0, 0.0, 0.0)

    override fun fork(): Chain<HEPParticle> {
        return ParticleGun().apply {
            TODO("Fork particle gun")
        }
    }

    override suspend fun next(): HEPParticle {
        return HEPParticle(definition, kineticEnergy, momentumDirection, position, 0.0)
    }

}




//class GeneralParticleSource: ParticleSource<HEPParticle>(){
//    override fun fork(): Chain<HEPParticle> {
//        return GeneralParticleSource().apply {
//            TODO("Fork particle gun")
//        }
//    }
//
//    override suspend fun next(): HEPParticle {
//        return HEPParticle(definition, kineticEnergy, momentumDirection, position, 0.0)
//    }
//}